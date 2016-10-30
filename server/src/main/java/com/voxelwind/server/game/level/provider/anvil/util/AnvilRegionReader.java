package com.voxelwind.server.game.level.provider.anvil.util;

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.google.common.base.Preconditions;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * This class reads an Anvil/McRegion region file.
 */
public class AnvilRegionReader implements Closeable {
    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = 1024;

    private final int totalSectorsAvailable;
    private final int[] offsets = new int[SECTOR_INTS];
    private final FileChannel channel;

    public AnvilRegionReader(Path path) throws IOException {
        this.channel = FileChannel.open(path, StandardOpenOption.READ);
        if (channel.size() <= SECTOR_BYTES * 2) {
            this.channel.close();
            throw new IOException("File is not a proper Anvil file.");
        }

        // Basic cell setup
        this.totalSectorsAvailable = (int) (this.channel.size() / SECTOR_BYTES);

        // Read the next 4096 bytes.
        ByteBuffer offsetsRead = ByteBuffer.allocate(SECTOR_BYTES);
        this.channel.read(offsetsRead);
        offsetsRead.flip();

        for (int i = 0; i < SECTOR_INTS; ++i) {
            int offset = offsetsRead.getInt();
            offsets[i] = offset;
        }

        // Skip the timestamps because Voxelwind doesn't care about them
        channel.position(channel.position() + SECTOR_BYTES);
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
    }

    public synchronized InputStream readChunk(int x, int z) throws IOException {
        Preconditions.checkArgument(!outOfBounds(x, z), "position (%s, %s) is out of range (0 through 32)", x, z);
        Preconditions.checkArgument(hasChunk(x, z), "chunk (%s, %s) does not exist", x, z);
        int offset = getOffset(x, z);

        int sectorNumber = offset >> 8;
        int numSectors = offset & 0xFF;

        if (sectorNumber + numSectors > totalSectorsAvailable) {
            throw new IllegalArgumentException("sector size is invalid for this chunk");
        }

        // Seek to the position in question.
        this.channel.position(sectorNumber * SECTOR_BYTES);

        // Read some basic information about this sector.
        ByteBuffer information = ByteBuffer.allocate(5);
        // 4 bytes: big-endian int is the size of this sector
        // 1 byte: compression type - 1 is gzip, 2 is deflate
        this.channel.read(information);
        information.flip();
        int sectorLength = information.getInt();
        byte type = information.get();

        ByteBuffer chunkData = ByteBuffer.allocate(sectorLength);
        this.channel.read(chunkData);
        chunkData.flip();

        switch (type) {
            case 1:
                return new GZIPInputStream(new ByteBufferBackedInputStream(chunkData), 2048);
            case 2:
                return new InflaterInputStream(new ByteBufferBackedInputStream(chunkData), new Inflater(), 2048);
            default:
                throw new IllegalArgumentException("found illegal chunk compression type " + type);
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
}
