package com.voxelwind.server.game.level.chunk;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.block.VoxelwindBlock;
import com.voxelwind.server.game.level.util.NibbleArray;
import com.voxelwind.server.network.mcpe.packets.McpeBatch;
import com.voxelwind.server.network.mcpe.packets.McpeFullChunkData;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class VoxelwindChunk implements Chunk {
    private static final int FULL_CHUNK_SIZE = 16 * 16 * 128; // 32768

    private final byte[] blockData = new byte[FULL_CHUNK_SIZE];
    private final NibbleArray blockMetadata = new NibbleArray(FULL_CHUNK_SIZE);
    private final NibbleArray skyLightData = new NibbleArray(FULL_CHUNK_SIZE);
    private final NibbleArray blockLightData = new NibbleArray(FULL_CHUNK_SIZE);

    private final int x;
    private final int z;
    private final byte[] biomeId = new byte[256];
    private final int[] biomeColor = new int[256];
    private final byte[] height = new byte[256];
    private McpeBatch chunkDataPacket;
    private boolean stale = true;

    public VoxelwindChunk(int x, int z) {
        this.x = x;
        this.z = z;
        Arrays.fill(biomeId, (byte) 1);
        Arrays.fill(biomeColor, 0x0185b24a);
        Arrays.fill(height, (byte) 0xFF);
    }

    private static int xyzIdx(int x, int y, int z) {
        return x + 16 * (z + 16 * y);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public synchronized Block getBlock(int x, int y, int z) {
        checkPosition(x, y, z);
        byte data = blockData[xyzIdx(x, y, z)];

        Vector3i full = new Vector3i(x + (this.x * 16), y, z + (this.z * 16));
        // TODO: Add level and associated block data
        return new VoxelwindBlock(null, this, full, new BasicBlockState(BlockTypes.forId(data)));
    }

    @Override
    public Block setType(int x, int y, int z, BlockState state) {
        setBlock(x, y, z, (byte) state.getBlockType().getId());
        return getBlock(x, y, z);
    }

    public synchronized void setBlock(int x, int y, int z, byte id) {
        checkPosition(x, y, z);
        byte old = blockData[xyzIdx(x, y, z)];
        if (old != id) {
            blockData[xyzIdx(x, y, z)] = id;
            stale = true;
        }
    }

    public synchronized McpeBatch getChunkDataPacket() {
        if (stale) {
            if (chunkDataPacket == null) {
                chunkDataPacket = new McpeBatch();
            } else {
                chunkDataPacket.releasePrecompressed();
                chunkDataPacket.getPackages().clear();
            }

            // Generate the inner data
            McpeFullChunkData data = new McpeFullChunkData();
            data.setChunkX(x);
            data.setChunkZ(z);
            data.setOrder((byte) 1);
            ByteArrayOutputStream memoryStream = new ByteArrayOutputStream(83204);

            try (DataOutputStream dos = new DataOutputStream(memoryStream)) {
                dos.write(blockData);
                dos.write(blockMetadata.getData());
                dos.write(skyLightData.getData());
                dos.write(blockLightData.getData());
                dos.write(height);
                for (int i : biomeColor) {
                    dos.writeInt(i);
                }
                // extra data, we have none
                dos.writeInt(0);
            } catch (IOException e) {
                throw new AssertionError(e);
            }

            // Finally, write an empty NBT compound.
            /*try (NBTOutputStream outputStream = new NBTOutputStream(memoryStream, false, ByteOrder.LITTLE_ENDIAN)) {
                outputStream.writeTag(new ListTag<>("", CompoundTag.class, ImmutableList.of()));
            } catch (IOException e) {
                throw new AssertionError(e);
            }*/

            data.setData(memoryStream.toByteArray());
            chunkDataPacket.getPackages().add(data);
            chunkDataPacket.precompress();
        }

        return chunkDataPacket;
    }

    public void writeTo(OutputStream stream) {
        try (DataOutputStream dos = new DataOutputStream(stream)) {
            dos.write(blockData);
            dos.write(blockMetadata.getData());
            dos.write(skyLightData.getData());
            dos.write(blockLightData.getData());
            dos.write(height);
            for (int i : biomeColor) {
                dos.writeInt(i);
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
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
