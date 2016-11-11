package com.voxelwind.server.game.level.chunk.provider.anvil;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.ChunkSnapshot;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.*;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.nbt.io.NBTEncoding;
import com.voxelwind.nbt.io.NBTWriter;
import com.voxelwind.nbt.tags.CompoundTag;
import com.voxelwind.nbt.tags.IntTag;
import com.voxelwind.nbt.util.SwappedDataOutputStream;
import com.voxelwind.server.game.level.chunk.util.FullChunkPacketCreator;
import com.voxelwind.server.game.serializer.MetadataSerializer;
import com.voxelwind.server.network.mcpe.packets.McpeBatch;
import com.voxelwind.server.network.mcpe.packets.McpeFullChunkData;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Synchronized;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

public class AnvilChunk extends AnvilChunkSnapshot implements Chunk, FullChunkPacketCreator {
    private final Level level;
    private final TIntObjectMap<CompoundTag> serializedBlockEntities = new TIntObjectHashMap<>();
    private McpeBatch precompressed;

    public AnvilChunk(ChunkSection[] sections, int x, int z, Level level) {
        super(sections, x, z);
        this.level = level;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    @Synchronized
    public Block getBlock(int x, int y, int z) {
        return (Block) super.getBlock(level, this, x, y, z);
    }

    @Override
    public Block setBlock(int x, int y, int z, BlockState state) {
        return setBlock(x, y, z, state, true);
    }

    @Override
    @Synchronized
    public Block setBlock(int x, int y, int z, BlockState state, boolean shouldRecalculateLight) {
        Preconditions.checkNotNull(state, "state");
        checkPosition(x, y, z);

        ChunkSection section = sections[y / 16];
        if (section == null) {
            section = new ChunkSection();
            sections[y / 16] = section;
        }
        section.setBlockId(x, y % 16, z, (byte) state.getBlockType().getId());
        section.setBlockData(x, y % 16, z, (byte) MetadataSerializer.serializeMetadata(state));
        //section.setBlockLight(x, y % 16, z, (byte) state.getBlockType().emitsLight());

        if (shouldRecalculateLight) {
            // Recalculate the height map and lighting for this chunk section.
            if (height[(z << 4) + x] <= y && state.getBlockType() != BlockTypes.AIR) {
                // Slight optimization
                height[(z << 4) + x] = (byte) y;
            } else {
                height[(z << 4) + x] = (byte) calculateHighestLayer(x, z);
            }

            populateSkyLightAt(x, z);
        } else {
            // If this is the quick case, then always update the height map.
            if (height[(z << 4) + x] <= y && state.getBlockType() != BlockTypes.AIR) {
                height[(z << 4) + x] = (byte) y;
            }
        }

        // now set the block entity, if any
        int pos = xyzIdx(x, y, z);
        Optional<BlockEntity> entity = state.getBlockEntity();
        if (entity.isPresent()) {
            CompoundTag blockEntityTag = MetadataSerializer.serializeNBT(state);
            blockEntityTag.getValue().put("x", new IntTag("x", x + (this.x * 16)));
            blockEntityTag.getValue().put("y", new IntTag("y", y));
            blockEntityTag.getValue().put("z", new IntTag("z", z + (this.z * 16)));
            serializedBlockEntities.put(pos, blockEntityTag);
            blockEntities.put(pos, entity.get());
        } else {
            serializedBlockEntities.remove(pos);
            blockEntities.remove(pos);
        }

        precompressed = null;
        return getBlock(x, y, z);
    }

    private int calculateHighestLayer(int x, int z) {
        for (int i = sections.length - 1; i >= 0; i--) {
            ChunkSection section = sections[i];
            if (section != null) {
                for (int j = 15; j >= 0; j--) {
                    if (section.getBlockId(x, j, z) != 0) {
                        return j + (i * 16);
                    }
                }
            }
        }
        return 0;
    }

    private void populateSkyLightAt(int x, int z) {
        int maxHeight = height[(z << 4) + x];

        // There's no blocks above this block, so it's always 15.
        for (int y = 127; y > maxHeight; y--) {
            sections[y / 16].setSkyLight(x, y % 16, z, (byte) 15);
        }

        // From the top, however...
        boolean blocked = false;
        for (int y = maxHeight; y > 0; y--) {
            BlockType type = BlockTypes.forId(sections[y / 16].getBlockId(x, y % 16, z));
            byte light = 15;
            if (!blocked) {
                if (!type.isTransparent()) {
                    blocked = true;
                    light = 0;
                }
            } else {
                light = 0;
            }

            sections[y / 16].setSkyLight(x, y % 16, z, light);
        }
    }

    @Override
    @Synchronized
    public ChunkSnapshot toSnapshot() {
        ChunkSection[] sections = this.sections.clone();
        for (int i = 0; i < sections.length; i++) {
            sections[i] = sections[i].copy();
        }
        AnvilChunkSnapshot snapshot = new AnvilChunkSnapshot(sections, x, z);
        System.arraycopy(biomeColor, 0, snapshot.biomeColor, 0, biomeColor.length);
        System.arraycopy(height, 0, snapshot.height, 0, height.length);
        snapshot.blockEntities.putAll(blockEntities); // TODO: This needs to be better
        return snapshot;
    }

    @Override
    @Synchronized
    public McpeBatch toFullChunkData() {
        if (precompressed != null) {
            return precompressed;
        }

        McpeFullChunkData data = new McpeFullChunkData();
        data.setChunkX(x);
        data.setChunkZ(z);
        data.setOrder((byte) 1);

        // Write out block entities first.
        CanWriteToBB blockEntities = null;
        int nbtSize = 0;
        if (!serializedBlockEntities.isEmpty()) {
            blockEntities = new CanWriteToBB();
            try (NBTWriter writer = new NBTWriter(new SwappedDataOutputStream(blockEntities), NBTEncoding.MCPE_0_16_NETWORK)) {
                // Write out NBT compounds for all block entities.
                for (CompoundTag entity : serializedBlockEntities.valueCollection()) {
                    writer.write(entity);
                }
            } catch (IOException e) {
                throw new AssertionError(e);
            }
            nbtSize = blockEntities.size();
        }

        ByteBuffer buffer = ByteBuffer.allocate(83202 + nbtSize);
        // Write the chunk sections.
        for (int i = 0; i < sections.length; i++) {
            ChunkSection section = sections[i];
            if (section != null) {
                buffer.position(10240 * i);
                buffer.put(section.getIds());
                buffer.put(section.getData().getData());
                buffer.put(section.getBlockLight().getData());
                buffer.put(section.getSkyLight().getData());
            }
        }

        buffer.position(81920);
        buffer.put(height);
        for (int i = 0; i < biomeColor.length; i++) {
            //int color = biomeColor[i];
            //byte biome = biomeId[i];
            //Varints.encodeSigned(buf, (color & 0x00ffffff) | biome << 2);
            buffer.putInt(biomeColor[i]);
        }
        // extra data, we have none
        buffer.putShort((short) 0);

        if (blockEntities != null) {
            blockEntities.writeTo(buffer);
        }

        data.setData(buffer.array());
        McpeBatch precompressed = new McpeBatch();
        precompressed.getPackages().add(data);
        precompressed.precompress();
        precompressed.getPackages().clear();
        this.precompressed = precompressed;
        return precompressed;
    }

    /**
     * Special version of {@link ByteArrayOutputStream} that can directly write its output to a {@link ByteBuffer}.
     */
    private static class CanWriteToBB extends ByteArrayOutputStream {
        public CanWriteToBB() {
            super(8192);
        }

        public void writeTo(ByteBuffer byteBuffer) {
            byteBuffer.put(buf, 0, count);
        }
    }
}
