package com.voxelwind.server.game.level.chunk;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.ChunkSnapshot;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.*;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.block.VoxelwindBlock;
import com.voxelwind.server.game.level.util.NibbleArray;
import com.voxelwind.server.network.mcpe.packets.McpeBatch;
import com.voxelwind.server.network.mcpe.packets.McpeFullChunkData;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VoxelwindChunk implements Chunk {
    private static final int FULL_CHUNK_SIZE = 16 * 16 * 128; // 32768

    private final byte[] blockData = new byte[FULL_CHUNK_SIZE];
    private final NibbleArray blockMetadata = new NibbleArray(FULL_CHUNK_SIZE);
    private final NibbleArray skyLightData = new NibbleArray(FULL_CHUNK_SIZE);
    private final NibbleArray blockLightData = new NibbleArray(FULL_CHUNK_SIZE);
    private final Map<Vector3i, BlockEntity> blockEntities = new HashMap<>();

    private final Level level;
    private final int x;
    private final int z;
    private final byte[] biomeId = new byte[256];
    private final int[] biomeColor = new int[256];
    private final byte[] height = new byte[256];
    private McpeBatch chunkDataPacket;
    private boolean stale = true;

    public VoxelwindChunk(Level level, int x, int z) {
        this.level = level;
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

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public synchronized Block getBlock(int x, int y, int z) {
        checkPosition(x, y, z);
        int index = xyzIdx(x, y, z);
        byte data = blockData[index];

        Vector3i full = new Vector3i(x + (this.x * 16), y, z + (this.z * 16));

        BlockType type = BlockTypes.forId(data);
        Optional<BlockData> createdData = type.createBlockDataFor(blockMetadata.get(index));

        // TODO: Add level and associated block data
        return new VoxelwindBlock(null, this, full, new BasicBlockState(BlockTypes.forId(data), createdData.orElse(null)), blockEntities.get(
                new Vector3i(x, y, z)));
    }

    @Override
    public synchronized Block setBlock(int x, int y, int z, BlockState state) {
        checkPosition(x, y, z);
        Preconditions.checkNotNull(state, "state");
        int index = xyzIdx(x, y, z);

        blockData[index] = (byte) state.getBlockType().getId();
        BlockData blockData = state.getBlockData();
        if (blockData != null) {
            blockMetadata.set(index, (byte) blockData.toBlockMetadata());
        } else {
            blockMetadata.set(index, (byte) 0);
        }

        stale = true;
        return getBlock(x, y, z);
    }

    @Override
    public ChunkSnapshot toSnapshot() {
        // TODO: Do a better job of this
        return new VoxelwindChunkSnapshot(
                Arrays.copyOf(blockData, blockData.length),
                blockMetadata.copy(),
                skyLightData.copy(),
                blockLightData.copy(),
                ImmutableMap.copyOf(blockEntities),
                x,
                z
        );
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
            stale = false;
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
        Preconditions.checkArgument(x >= 0 && x <= 15, "x value (%s) not in range (0 to 15)", x);
        Preconditions.checkArgument(z >= 0 && z <= 15, "z value (%s) not in range (0 to 15)", z);
        Preconditions.checkArgument(y >= 0 && y <= 128, "y value (%s) not in range (0 to 128)", y);
    }
}
