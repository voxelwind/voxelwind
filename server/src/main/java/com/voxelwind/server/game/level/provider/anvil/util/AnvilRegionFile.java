package com.voxelwind.server.game.level.provider.anvil.util;

import com.google.common.base.Preconditions;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.BitSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * This class manipulates an Anvil/McRegion region file.
 */
public class AnvilRegionFile implements Closeable {
    private static final int SECTOR_HEADER_SIZE = 5;
    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = 1024;
    private static final ByteBuffer EMPTY_SECTOR = ByteBuffer.allocateDirect(SECTOR_BYTES).asReadOnlyBuffer();

    private int totalSectorsAvailable;
    private final int[] offsets = new int[SECTOR_INTS];
    private final int[] timestamps = new int[SECTOR_INTS];
    private final BitSet usedSectors = new BitSet();
    private final FileChannel channel;

    public AnvilRegionFile(Path path) throws IOException {
        this.channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
        if (channel.size() <= SECTOR_BYTES * 2) {
            // Fill the file with zeros
            if (channel.size() == 0) {
                // Fast path
                channel.write(new ByteBuffer[]{ EMPTY_SECTOR.duplicate(), EMPTY_SECTOR.duplicate() });
            } else {
                channel.write(ByteBuffer.allocate((int) (SECTOR_BYTES * 2 - channel.size())));
            }
        }

        if ((channel.size() & 0xfff) != 0) {
            // Not a multiple of 4KB, so grow it.
            channel.write(ByteBuffer.allocate((int) (SECTOR_BYTES - (channel.size() & 0xfff))));
        }

        channel.position(0);
        // Basic cell setup
        this.totalSectorsAvailable = (int) (this.channel.size() / SECTOR_BYTES);
        usedSectors.set(0, 2);

        // Read the next 4096 bytes.
        ByteBuffer offsets = ByteBuffer.allocate(SECTOR_BYTES * 2);
        while (offsets.hasRemaining()) {
            if (this.channel.read(offsets) == -1) {
                throw new EOFException();
            }
        }
        this.channel.read(offsets);
        offsets.flip();
        IntBuffer offsetInts = offsets.asIntBuffer();

        for (int i = 0; i < SECTOR_INTS; ++i) {
            this.offsets[i] = offsetInts.get();
            int sectorNumber = this.offsets[i] >> 8;
            int occupiedSectors = this.offsets[i] & 0xFF;
            if (this.offsets[i] != 0 && sectorNumber >= 0 && sectorNumber + occupiedSectors <= totalSectorsAvailable) {
                usedSectors.set(sectorNumber, sectorNumber + occupiedSectors + 1);
            }
        }

        for (int i = 0; i < SECTOR_INTS; i++) {
            this.timestamps[i] = offsetInts.get();
        }
    }

    @Override
    public void close() throws IOException {
        this.channel.force(true);
        this.channel.close();
    }

    public synchronized InputStream readChunk(int x, int z) throws IOException {
        Preconditions.checkArgument(!outOfBounds(x, z), "position (%s, %s) is out of range (0 through 32)", x, z);
        Preconditions.checkArgument(hasChunk(x, z), "chunk (%s, %s) does not exist", x, z);
        int offset = getOffset(x, z);

        int sectorNumber = offset >> 8;
        int occupiedSectors = offset & 0xFF;

        if (sectorNumber + occupiedSectors > totalSectorsAvailable) {
            throw new IllegalArgumentException("sector size is invalid for this chunk");
        }

        // Seek to the position in question.
        this.channel.position(sectorNumber * SECTOR_BYTES);

        // Read the entire sector.
        ByteBuffer sector = ByteBuffer.allocate(occupiedSectors * SECTOR_BYTES);
        while (sector.hasRemaining()) {
            if (this.channel.read(sector) == -1) {
                throw new EOFException();
            }
        }
        sector.clear();
        // 4 bytes: big-endian int is the size of this sector
        // 1 byte: compression type - 1 is gzip, 2 is deflate
        int sectorLength = sector.getInt();
        if (sectorLength > sector.capacity()) {
            throw new IOException("Mismatched sector length (read " + occupiedSectors + " sectors, but length is " + sectorLength + " bytes)");
        }
        byte type = sector.get();

        switch (type) {
            case 1:
                return new BufferedInputStream(new GZIPInputStream(
                        new ByteArrayInputStream(sector.array(), sector.arrayOffset() + sector.position(), sectorLength - 1)));
            case 2:
                return new BufferedInputStream(new InflaterInputStream(
                        new ByteArrayInputStream(sector.array(), sector.arrayOffset() + sector.position(), sectorLength - 1)));
            default:
                throw new IllegalArgumentException("found illegal chunk compression type " + type);
        }
    }

    public synchronized void writeChunk(int x, int z, ByteBuffer buffer) throws IOException {
        Preconditions.checkArgument(!outOfBounds(x, z), "position (%s, %s) is out of range (0 through 32)", x, z);

        int offset = getOffset(x, z);
        int sectorNumber = offset >> 8;
        int sectorsAllocated = offset & 0xFF;
        int sectorsNeeded = (buffer.remaining() + SECTOR_HEADER_SIZE) / SECTOR_BYTES + 1;

        if (sectorsNeeded >= 256) {
            throw new IllegalArgumentException("Writing this chunk would take too many sectors (limit is 255, but need " + sectorsNeeded + ")");
        }

        if (sectorNumber != 0 && sectorsAllocated == sectorsNeeded) {
            // We can overwrite the old sector(s).
            writeChunkInternal(sectorNumber, buffer);
        } else {
            // We must find new sectors to use. Clear currently allocated sectors.
            for (int i = 0; i < sectorsAllocated; ++i) {
                usedSectors.clear(sectorNumber + i);
            }

            // Now search for the required number of sectors.
            int runStart = 2;
            int runLength = 0;
            int currentSector = 2;
            while (runLength < sectorsNeeded) {
                if (usedSectors.length() >= currentSector) {
                    // We reached the end, and we will need to allocate a new sector.
                    break;
                }
                int nextSector = usedSectors.nextClearBit(currentSector + 1);
                if (currentSector + 1 == nextSector) {
                    runLength++;
                } else {
                    runStart = nextSector;
                    runLength = 1;
                }
                currentSector = nextSector;
            }

            // If we have enough sectors, then use it.
            if (runLength >= sectorsNeeded) {
                setOffset(x, z, runStart, sectorsNeeded);
                for (int i = 0; i < sectorsNeeded; ++i) {
                    usedSectors.set(sectorNumber + i);
                }
                writeChunkInternal(runStart, buffer);
            } else {
                // Need to allocate new sectors.
                int startSector = totalSectorsAvailable;
                channel.position(channel.size());
                usedSectors.set(startSector, startSector + sectorsNeeded + 1);

                ByteBuffer[] buffers = new ByteBuffer[sectorsNeeded];
                for (int i = 0; i < buffers.length; i++) {
                    buffers[i] = EMPTY_SECTOR.duplicate();
                }

                channel.write(buffers);
                totalSectorsAvailable += sectorsNeeded;
                setOffset(x, z, startSector, sectorsNeeded);
                writeChunkInternal(startSector, buffer);
            }
        }
    }

    private void writeChunkInternal(int startSector, ByteBuffer buffer) throws IOException {
        ByteBuffer header = ByteBuffer.allocateDirect(SECTOR_HEADER_SIZE);
        header.putInt(buffer.remaining() + 1).put((byte) 2);
        header.flip();

        channel.position(startSector * SECTOR_BYTES);

        long bytesToWrite = buffer.remaining() + header.remaining();
        ByteBuffer[] toWrite = new ByteBuffer[]{ header, buffer };
        while (bytesToWrite > 0) {
            long written = channel.write(toWrite);
            bytesToWrite -= written;
            if (written == 0) {
                throw new IOException("Can't write any more chunks");
            }
        }
    }

    private static boolean outOfBounds(int x, int z) {
        return x < 0 || x >= 32 || z < 0 || z >= 32;
    }

    private int getOffset(int x, int z) {
        return offsets[x + z * 32];
    }

    public boolean hasChunk(int x, int z) {
        return getOffset(x, z) != 0;
    }

    private void setOffset(int x, int z, int sectorStart, int sectorsOccupied) throws IOException {
        int num = x + z * 32;
        int encoded = sectorStart << 8 | sectorsOccupied;
        offsets[num] = encoded;
        ByteBuffer offsetNumber = ByteBuffer.allocate(Integer.BYTES).putInt(encoded);
        offsetNumber.flip();

        channel.position(num * 4);
        while (offsetNumber.hasRemaining()) {
            if (channel.write(offsetNumber) == 0) {
                throw new IOException("Can't write any more of the offset buffer!");
            }
        }
    }

    private void setTimestamp(int x, int z, int timestamp) throws IOException {
        int num = x + z * 32;
        timestamps[num] = timestamp;
        ByteBuffer tsNumber = ByteBuffer.allocate(Integer.BYTES).putInt(timestamp);
        tsNumber.flip();

        channel.position(SECTOR_BYTES + num * 4);
        while (tsNumber.hasRemaining()) {
            if (channel.write(tsNumber) == 0) {
                throw new IOException("Can't write any more of the timestamp buffer!");
            }
        }
    }
}
