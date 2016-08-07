package com.voxelwind.server.level.chunk;

import com.voxelwind.server.level.util.NibbleArray;
import com.voxelwind.server.network.mcpe.packets.McpeFullChunkData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Chunk {
    private static final int FULL_CHUNK_SIZE = 16 * 16 * 128; // 32768

    private final byte[] blockData = new byte[FULL_CHUNK_SIZE];
    private final NibbleArray blockMetadata = new NibbleArray(FULL_CHUNK_SIZE);
    private final NibbleArray skyLightData = new NibbleArray(FULL_CHUNK_SIZE);
    private final NibbleArray blockLightData = new NibbleArray(FULL_CHUNK_SIZE);

    private final int x;
    private final int z;
    private final byte[] biomeId = new byte[256];
    private final int[] biomeColor = new int[256];
    //private final byte[] height = new byte[256];
    private McpeFullChunkData chunkDataPacket;
    private boolean stale = true;

    public Chunk(int x, int z) {
        this.x = x;
        this.z = z;
        Arrays.fill(biomeId, (byte) 1);
        Arrays.fill(biomeColor, 0x0185b24a);
    }

    private static int xyzIdx(int x, int y, int z) {
        return (x * 2048) + (z * 128) + y;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public synchronized byte getBlock(int x, int y, int z) {
        checkPosition(x, y, z);
        return blockData[xyzIdx(x, y, z)];
    }

    public synchronized void setBlock(int x, int y, int z, byte id) {
        checkPosition(x, y, z);
        byte old = blockData[xyzIdx(x, y, z)];
        if (old != id) {
            blockData[xyzIdx(x, y, z)] = id;
            stale = true;
        }
    }

    public synchronized McpeFullChunkData getChunkDataPacket() {
        if (stale || chunkDataPacket == null) {
            if (chunkDataPacket == null) {
                chunkDataPacket = new McpeFullChunkData();
                chunkDataPacket.setChunkX(x);
                chunkDataPacket.setChunkZ(z);
            }

            // Generate the inner data
            ByteArrayOutputStream memoryStream = new ByteArrayOutputStream(83204);

            try (DataOutputStream dos = new DataOutputStream(memoryStream)) {
                dos.write(blockData);
                dos.write(blockMetadata.getData());
                dos.write(skyLightData.getData());
                dos.write(blockLightData.getData());
                for (int i = 0; i < 256; i++) {
                    dos.writeByte(0xFF);
                }
                for (int i : biomeColor) {
                    dos.writeInt(i);
                }
                dos.writeInt(0);
            } catch (IOException e) {
                throw new AssertionError(e);
            }

            chunkDataPacket.setData(memoryStream.toByteArray());
        }

        return chunkDataPacket;
    }

    private static void checkPosition(int x, int y, int z) {
        if (x < 0 || x >= 16) {
            throw new IllegalArgumentException("x not in range (0 to 15)");
        }

        if (z < 0 || z >= 16) {
            throw new IllegalArgumentException("z not in range (0 to 15)");
        }

        if (y < 0 || y > 128) {
            throw new IllegalArgumentException("y not in range (0 to 128)");
        }
    }
}
