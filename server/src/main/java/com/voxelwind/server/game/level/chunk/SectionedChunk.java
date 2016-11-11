package com.voxelwind.server.game.level.chunk;

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
import com.voxelwind.nbt.tags.Tag;
import com.voxelwind.nbt.util.SwappedDataOutputStream;
import com.voxelwind.server.game.level.chunk.provider.anvil.ChunkSection;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class stores chunk data in sections of 16x16x16 sections, with each section eventually representing a 16x128x16
 * chunk.
 */
public class SectionedChunk extends SectionedChunkSnapshot implements Chunk, FullChunkPacketCreator {
    private final Level level;
    private final TIntObjectMap<CompoundTag> serializedBlockEntities = new TIntObjectHashMap<>();
    private McpeBatch precompressed;

    public SectionedChunk(int x, int z, Level level) {
        this(new ChunkSection[8], x, z, level);
    }

    public SectionedChunk(ChunkSection[] sections, int x, int z, Level level) {
        super(sections, x, z);
        this.level = level;
        Arrays.fill(biomeColor, 0x0185b24a);
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
        }

        // now set the block entity, if any
        int pos = xyzIdx(x, y, z);
        Optional<BlockEntity> entity = state.getBlockEntity();
        if (entity.isPresent()) {
            CompoundTag blockEntityTag = MetadataSerializer.serializeNBT(state);
            Map<String, Tag<?>> beModifiedMap = new HashMap<>(blockEntityTag.getValue());
            beModifiedMap.put("x", new IntTag("x", x + (this.x * 16)));
            beModifiedMap.put("y", new IntTag("y", y));
            beModifiedMap.put("z", new IntTag("z", z + (this.z * 16)));
            serializedBlockEntities.put(pos, new CompoundTag("", beModifiedMap));
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
            ChunkSection section = sections[y / 16];
            if (section != null) {
                section.setSkyLight(x, y % 16, z, (byte) 15);
            }
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

            ChunkSection section = sections[y / 16];
            if (section != null) {
                section.setSkyLight(x, y % 16, z, light);
            }
        }
    }

    @Override
    @Synchronized
    public ChunkSnapshot toSnapshot() {
        ChunkSection[] sections = this.sections.clone();
        for (int i = 0; i < sections.length; i++) {
            sections[i] = sections[i].copy();
        }
        SectionedChunkSnapshot snapshot = new SectionedChunkSnapshot(sections, x, z);
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
                buffer.position(4096 * i);
                buffer.put(section.getIds());
                buffer.position(32768 + 2048 * i);
                buffer.put(section.getData().getData());
                buffer.position(49152 + 2048 * i);
                buffer.put(section.getSkyLight().getData());
                buffer.position(65536 + 2048 * i);
                buffer.put(section.getBlockLight().getData());
            }
        }

        buffer.position(81920);
        buffer.put(height);
        buffer.asIntBuffer().put(biomeColor);
        /*for (int i = 0; i < biomeColor.length; i++) {
            //int color = biomeColor[i];
            //byte biome = biomeId[i];
            //Varints.encodeSigned(buf, (color & 0x00ffffff) | biome << 2);
            buffer.putInt(biomeColor[i]);
        }*/
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

    @Synchronized
    public void recalculateLight() {
        recalculateHeightMap();
        populateSkyLight();
        precompressed = null;
    }

    private void recalculateHeightMap() {
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int highest = calculateHighestLayer(x, z);
                height[(z << 4) + x] = (byte) highest;
            }
        }
    }

    private void populateSkyLight() {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                populateSkyLightAt(x, z);
            }
        }
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
