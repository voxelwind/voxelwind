package com.voxelwind.server.game.level.chunk;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.ChunkSnapshot;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.nbt.io.NBTEncoding;
import com.voxelwind.nbt.io.NBTWriter;
import com.voxelwind.nbt.tags.CompoundTag;
import com.voxelwind.nbt.tags.IntTag;
import com.voxelwind.nbt.tags.Tag;
import com.voxelwind.nbt.util.SwappedDataOutputStream;
import com.voxelwind.server.game.level.chunk.util.FullChunkPacketCreator;
import com.voxelwind.server.game.serializer.MetadataSerializer;
import com.voxelwind.server.network.mcpe.packets.McpeFullChunkData;
import com.voxelwind.server.network.mcpe.packets.McpeWrapper;
import com.voxelwind.server.network.util.CompressionUtil;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import lombok.Synchronized;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * This class stores chunk data in sections of 16x16x16 sections, with each section eventually representing a 16x128x16
 * chunk.
 */
public class SectionedChunk extends SectionedChunkSnapshot implements Chunk, FullChunkPacketCreator {
    private final Level level;
    private final TIntObjectMap<CompoundTag> serializedBlockEntities = new TIntObjectHashMap<>();
    private McpeWrapper precompressed;

    public SectionedChunk(int x, int z, Level level) {
        this(new ChunkSection[16], x, z, level);
    }

    public SectionedChunk(ChunkSection[] sections, int x, int z, Level level) {
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

        ChunkSection section = getOrCreateSection(y / 16);
        section.setBlockId(x, y % 16, z, (byte) state.getBlockType().getId());
        section.setBlockData(x, y % 16, z, (byte) MetadataSerializer.serializeMetadata(state));

        if (shouldRecalculateLight) {
            // Recalculate the height map and lighting for this chunk section.
            if (height[(z << 4) + x] <= y && state.getBlockType() != BlockTypes.AIR) {
                // Slight optimization
                height[(z << 4) + x] = (byte) y;
            } else {
                height[(z << 4) + x] = (byte) calculateHighestLayer(x, z);
            }

            populateSkyLightAt(x, z);
            calculateBlockLight(x, y, z);
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

    @Synchronized
    public ChunkSection getOrCreateSection(int y) {
        if (y >= sections.length) {
            throw new IllegalArgumentException("expected y to be up to " + (sections.length - 1) + ", got " + y);
        }
        ChunkSection section = sections[y];
        if (section == null) {
            sections[y] = section = new ChunkSection();
        }
        return section;
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
            if (sections[i] != null) {
                sections[i] = sections[i].copy();
            }
        }
        SectionedChunkSnapshot snapshot = new SectionedChunkSnapshot(sections, x, z);
        System.arraycopy(biomeId, 0, snapshot.biomeId, 0, biomeId.length);
        System.arraycopy(height, 0, snapshot.height, 0, height.length);
        snapshot.blockEntities.putAll(blockEntities); // TODO: This needs to be better
        return snapshot;
    }

    @Override
    @Synchronized
    public McpeWrapper toFullChunkData() {
        if (precompressed != null) {
            return precompressed;
        }

        McpeFullChunkData data = new McpeFullChunkData();
        data.setChunkX(x);
        data.setChunkZ(z);

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

        int topBlank = 0;
        for (int i = sections.length - 1; i >= 0; i--) {
            ChunkSection section = sections[i];
            if (section == null || section.isEmpty()) {
                topBlank = i + 1;
            } else {
                break;
            }
        }

        int bufferSize = 1 + 10241 * topBlank + 768 + 2 + nbtSize;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.put((byte) topBlank);

        // Write the chunk sections.
        for (int i = 0; i < topBlank; i++) {
            ChunkSection section = sections[i];
            buffer.put((byte) 0);
            if (section != null) {
                buffer.put(section.getIds());
                buffer.put(section.getData().getData());
                buffer.put(section.getSkyLight().getData());
                buffer.put(section.getBlockLight().getData());
            } else {
                buffer.position(buffer.position() + 10240);
            }
        }

        buffer.put(height);
        buffer.put(biomeId);

        // extra data, we have none
        buffer.putShort((short) 0);

        if (blockEntities != null) {
            blockEntities.writeTo(buffer);
        }

        data.setData(buffer.array());
        McpeWrapper precompressed = new McpeWrapper();
        precompressed.setPayload(CompressionUtil.compressWrapperPackets(data));
        this.precompressed = precompressed;
        return precompressed;
    }

    @Synchronized
    public void recalculateLight() {
        recalculateHeightMap();
        populateSkyLight();
        precompressed = null;
    }

    @Synchronized
    public void recalculateHeightMap() {
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

    private void calculateBlockLight(int x, int y, int z) {
        // NB: This will benefit from Java 9 value types. However, that won't be until late 2017...
        Queue<Vector3i> spread = new ArrayDeque<>();
        Queue<LightRemoveData> remove = new ArrayDeque<>();
        TLongSet visitedSpread = new TLongHashSet();
        TLongSet visitedRemove = new TLongHashSet();

        ChunkSection section = getOrCreateSection(y / 16);
        BlockType ourType = BlockTypes.forId(section.getBlockId(x, y % 16, z));
        byte currentBlockLight = section.getBlockLight(x, y % 16, z);
        byte newBlockLight = (byte) ourType.emitsLight();

        if (currentBlockLight != newBlockLight) {
            // Set the current block's light.
            System.out.println("old light: " + currentBlockLight + ", new light: " + newBlockLight);
            section.setBlockLight(x, y % 16, z, newBlockLight);

            if (newBlockLight < currentBlockLight) {
                remove.add(new LightRemoveData(new Vector3i(x, y, z), currentBlockLight));
                visitedRemove.add(xyzIdx(x, y, z));
            } else {
                spread.add(new Vector3i(x, y, z));
                visitedSpread.add(xyzIdx(x, y, z));
            }
        }

        LightRemoveData toRemove;
        while ((toRemove = remove.poll()) != null) {
            computeRemoveBlockLight(toRemove.data.sub(1, 0, 0), toRemove.light, remove, spread, visitedRemove, visitedSpread);
            computeRemoveBlockLight(toRemove.data.add(1, 0, 0), toRemove.light, remove, spread, visitedRemove, visitedSpread);
            computeRemoveBlockLight(toRemove.data.sub(0, 1, 0), toRemove.light, remove, spread, visitedRemove, visitedSpread);
            computeRemoveBlockLight(toRemove.data.add(0, 1, 0), toRemove.light, remove, spread, visitedRemove, visitedSpread);
            computeRemoveBlockLight(toRemove.data.sub(0, 0, 1), toRemove.light, remove, spread, visitedRemove, visitedSpread);
            computeRemoveBlockLight(toRemove.data.add(0, 0, 1), toRemove.light, remove, spread, visitedRemove, visitedSpread);
        }

        Vector3i toSpread;
        while ((toSpread = spread.poll()) != null) {
            ChunkSection cs = getOrCreateSection(toSpread.getY() / 16);
            byte adjustedLight = (byte) (cs.getBlockLight(toSpread.getX(), toSpread.getY() % 16, toSpread.getZ())
                                - BlockTypes.forId(cs.getBlockId(toSpread.getX(), toSpread.getY() % 16, toSpread.getZ())).filtersLight());

            if(adjustedLight >= 1){
                computeSpreadBlockLight(toSpread.sub(1, 0, 0), adjustedLight, spread, visitedSpread);
                computeSpreadBlockLight(toSpread.add(1, 0, 0), adjustedLight, spread, visitedSpread);
                computeSpreadBlockLight(toSpread.sub(0, 1, 0), adjustedLight, spread, visitedSpread);
                computeSpreadBlockLight(toSpread.add(0, 1, 0), adjustedLight, spread, visitedSpread);
                computeSpreadBlockLight(toSpread.sub(0, 0, 1), adjustedLight, spread, visitedSpread);
                computeSpreadBlockLight(toSpread.add(0, 0, 1), adjustedLight, spread, visitedSpread);
            }
        }
    }

    private void computeRemoveBlockLight(Vector3i loc, byte currentLight, Queue<LightRemoveData> removalQueue, Queue<Vector3i> spreadQueue, TLongSet removalVisited, TLongSet spreadVisited) {
        if (loc.getY() >= 256) {
            return;
        }
        ChunkSection section = getOrCreateSection(loc.getY() / 16);
        byte presentLight = section.getBlockLight(loc.getX(), loc.getY() % 16, loc.getZ());
        long idx = xyzIdx(loc.getX(), loc.getY(), loc.getZ());
        if (presentLight != 0 && presentLight < currentLight) {
            section.setBlockLight(loc.getX(), loc.getY() % 16, loc.getZ(), (byte) 0);
            if (removalVisited.add(idx)) {
                if (presentLight > 1) {
                    removalQueue.add(new LightRemoveData(loc, presentLight));
                }
            }
        } else if (presentLight >= currentLight) {
            if (spreadVisited.add(idx)) {
                spreadQueue.add(loc);
            }
        }
    }

    private void computeSpreadBlockLight(Vector3i loc, byte currentLight, Queue<Vector3i> spreadQueue, TLongSet spreadVisited) {
        if (loc.getY() >= 256) {
            return;
        }
        ChunkSection section = getOrCreateSection(loc.getY() / 16);
        byte presentLight = section.getBlockLight(loc.getX(), loc.getY() % 16, loc.getZ());
        long idx = xyzIdx(loc.getX(), loc.getY(), loc.getZ());
        if (presentLight < currentLight) {
            section.setBlockLight(loc.getX(), loc.getY() % 16, loc.getZ(), currentLight);
            if (spreadVisited.add(idx)) {
                if (presentLight > 1) {
                    spreadQueue.add(loc);
                }
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

    private static class LightRemoveData {
        private final Vector3i data;
        private final byte light;

        private LightRemoveData(Vector3i data, byte light) {
            this.data = data;
            this.light = light;
        }
    }
}
