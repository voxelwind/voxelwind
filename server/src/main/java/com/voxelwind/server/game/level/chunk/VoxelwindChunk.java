package com.voxelwind.server.game.level.chunk;

import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.ChunkSnapshot;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.*;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.block.VoxelwindBlock;
import com.voxelwind.server.game.level.util.NibbleArray;
import com.voxelwind.server.game.serializer.MetadataSerializer;
import com.voxelwind.server.network.mcpe.packets.McpeBatch;
import com.voxelwind.server.network.mcpe.packets.McpeFullChunkData;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VoxelwindChunk implements Chunk {
    private static final int FULL_CHUNK_SIZE = 16 * 16 * 128; // 32768

    private final byte[] blockData = new byte[FULL_CHUNK_SIZE];
    private final NibbleArray blockMetadata = new NibbleArray(FULL_CHUNK_SIZE);
    private final NibbleArray skyLightData = new NibbleArray(FULL_CHUNK_SIZE);
    private final NibbleArray blockLightData = new NibbleArray(FULL_CHUNK_SIZE);
    private final TIntObjectMap<CompoundTag> serializedBlockEntities = new TIntObjectHashMap<>();
    private final TIntObjectMap<BlockEntity> blockEntities = new TIntObjectHashMap<>();

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
        Optional<Metadata> createdData;
        if (type.getMetadataClass() != null) {
            createdData = Optional.of(MetadataSerializer.deserializeMetadata(type, blockMetadata.get(index)));
        } else {
            createdData = Optional.empty();
        }

        return new VoxelwindBlock(level, this, full, new BasicBlockState(BlockTypes.forId(data), createdData.orElse(null), blockEntities.get(index)));
    }

    @Override
    public synchronized Block setBlock(int x, int y, int z, BlockState state) {
        return setBlock(x, y, z, state, true);
    }

    @Override
    public synchronized Block setBlock(int x, int y, int z, BlockState state, boolean shouldRecalculateLight) {
        checkPosition(x, y, z);
        Preconditions.checkNotNull(state, "state");

        // set up block data
        setBlockId(x, y, z, state.getBlockType().getId(), state.getBlockData() == null ? 0 : MetadataSerializer.serializeMetadata(state), shouldRecalculateLight);

        // now set the block entity, if any
        Optional<BlockEntity> entity = state.getBlockEntity();
        if (entity.isPresent()) {
            int pos = xyzIdx(x, y, z);
            CompoundTag blockEntityTag = MetadataSerializer.serializeNBT(state);
            blockEntityTag.getValue().put(new IntTag("x", x + (this.x * 16)));
            blockEntityTag.getValue().put(new IntTag("y", y));
            blockEntityTag.getValue().put(new IntTag("z", z + (this.z * 16)));
            serializedBlockEntities.put(pos, blockEntityTag);
            blockEntities.put(pos, entity.get());
        }

        return getBlock(x, y, z);
    }

    public synchronized void setBlockId(int x, int y, int z, int blockId, short metadata) {
        setBlockId(x, y, z, blockId, metadata, true);
    }

    public synchronized void setBlockId(int x, int y, int z, int blockId, short metadata, boolean shouldRecalculateLight) {
        checkPosition(x, y, z);
        int index = xyzIdx(x, y, z);

        blockData[index] = (byte) blockId;
        blockMetadata.set(index, (byte) metadata);

        if (shouldRecalculateLight) {
            // Recalculate the height map and lighting for this chunk section.
            if (height[(z << 4) + x] <= y && blockId != 0) {
                // Slight optimization
                height[(z << 4) + x] = (byte) y;
            } else {
                height[(z << 4) + x] = (byte) getHighestLayer(x, z);
            }

            populateSkyLightAt(x, z);
        } else {
            // If this is the quick case, then just update the height map.
            if (height[(z << 4) + x] <= y && blockId != 0) {
                height[(z << 4) + x] = (byte) y;
            }
        }

        // Remove the block entity that exists here.
        serializedBlockEntities.remove(index);
        blockEntities.remove(index);

        stale = true;
    }

    public synchronized void recalculateLight() {
        recalculateHeightMap();
        populateSkyLight();
    }

    private synchronized void recalculateHeightMap() {
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int highest = getHighestLayer(x, z);
                height[(z << 4) + x] = (byte) highest;
            }
        }
    }

    private synchronized void populateSkyLight() {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                populateSkyLightAt(x, z);
            }
        }
    }

    private synchronized void populateSkyLightAt(int x, int z) {
        int maxHeight = height[(z << 4) + x];

        // There's no blocks above this block, so it's always 15.
        for (int y = 127; y > maxHeight; y--) {
            skyLightData.set(xyzIdx(x, y, z), (byte) 15);
        }

        // From the top, however...
        for (int y = maxHeight; y > 0; y--) {
            BlockType type = BlockTypes.forId(blockData[y]);
            if (!type.isTransparent()) {
                break;
            }

            skyLightData.set(xyzIdx(x, y, z), (byte) 15);
        }
    }

    @Override
    public synchronized int getHighestLayer(int x, int z) {
        for (int i = 127; i > 0; i--) {
            if (blockData[xyzIdx(x, i, z)] != 0) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public ChunkSnapshot toSnapshot() {
        // TODO: Do a better job of this
        return new VoxelwindChunkSnapshot(
                Arrays.copyOf(blockData, blockData.length),
                blockMetadata.copy(),
                skyLightData.copy(),
                blockLightData.copy(),
                new TIntObjectHashMap<>(blockEntities),
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

            // Finally, write out block entity compounds for block entities;
            try (NBTOutputStream stream = new NBTOutputStream(memoryStream, false, ByteOrder.LITTLE_ENDIAN)) {
                if (serializedBlockEntities.isEmpty()) {
                    // Write out an empty root tag
                    stream.writeTag(new CompoundTag("", new CompoundMap()));
                } else {
                    // Write out NBT compounds for all block entities.
                    for (CompoundTag entity : serializedBlockEntities.valueCollection()) {
                        stream.writeTag(entity);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            data.setData(memoryStream.toByteArray());
            chunkDataPacket.getPackages().add(data);
            chunkDataPacket.precompress();
            stale = false;
        }

        return chunkDataPacket;
    }

    public void writeTo(OutputStream stream) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(stream)) {
            dos.write(blockData);
            dos.write(blockMetadata.getData());
            dos.write(skyLightData.getData());
            dos.write(blockLightData.getData());
            dos.write(height);
            for (int i : biomeColor) {
                dos.writeInt(i);
            }
        }
    }

    private static void checkPosition(int x, int y, int z) {
        Preconditions.checkArgument(x >= 0 && x <= 15, "x value (%s) not in range (0 to 15)", x);
        Preconditions.checkArgument(z >= 0 && z <= 15, "z value (%s) not in range (0 to 15)", z);
        Preconditions.checkArgument(y >= 0 && y < 128, "y value (%s) not in range (0 to 127)", y);
    }
}
