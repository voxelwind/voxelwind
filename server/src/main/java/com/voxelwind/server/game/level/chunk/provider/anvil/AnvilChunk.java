package com.voxelwind.server.game.level.chunk.provider.anvil;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.voxelwind.api.game.Metadata;
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
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.block.VoxelwindBlock;
import com.voxelwind.server.game.level.chunk.util.FullChunkPacketCreator;
import com.voxelwind.server.game.serializer.MetadataSerializer;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.PooledByteBufAllocator;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Optional;

public class AnvilChunk implements Chunk, FullChunkPacketCreator {
    private static final byte[] EMPTY_2048 = new byte[2048];

    private final ChunkSection[] sections;
    private final int x;
    private final int z;
    private final Level level;
    private final int[] biomeColor = new int[256];
    private final byte[] height = new byte[256];
    private final TIntObjectMap<CompoundTag> serializedBlockEntities = new TIntObjectHashMap<>();
    private final TIntObjectMap<BlockEntity> blockEntities = new TIntObjectHashMap<>();

    public AnvilChunk(ChunkSection[] sections, int x, int z, Level level) {
        this.sections = sections;
        this.x = x;
        this.z = z;
        this.level = level;
    }

    static int xyzIdx(int x, int y, int z) {
        return x + 16 * (z + 16 * y);
    }

    private Vector3i getLevelLocation(int chunkX, int y, int chunkZ) {
        return new Vector3i(chunkX + (this.x * 16), y, chunkZ + (this.z * 16));
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        checkPosition(x, y, z);
        ChunkSection section = sections[y / 16];
        Vector3i full = getLevelLocation(x, y, z);

        if (section == null) {
            return new VoxelwindBlock(level, this, full, new BasicBlockState(BlockTypes.AIR, null, null));
        }

        BlockType type = BlockTypes.forId(section.getBlockId(x, y % 16, z));
        Optional<Metadata> createdData;
        if (type.getMetadataClass() != null) {
            createdData = Optional.of(MetadataSerializer.deserializeMetadata(type, section.getBlockData(x, y % 16, z)));
        } else {
            createdData = Optional.empty();
        }

        return new VoxelwindBlock(level, this, full, new BasicBlockState(type, createdData.orElse(null), blockEntities.get(xyzIdx(x, y, z))));
    }

    @Override
    public Block setBlock(int x, int y, int z, BlockState state) {
        return setBlock(x, y, z, state, true);
    }

    @Override
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

        // If this is the quick case, then always update the height map.
        if (height[(z << 4) + x] <= y && state.getBlockType() != BlockTypes.AIR) {
            height[(z << 4) + x] = (byte) y;
        }

        if (shouldRecalculateLight) {
            // Recalculate the height map and lighting for this chunk section.
            if (height[(z << 4) + x] <= y && state.getBlockType() != BlockTypes.AIR) {
                // Slight optimization
                height[(z << 4) + x] = (byte) y;
            } else {
                height[(z << 4) + x] = (byte) getHighestLayer(x, z);
            }

            populateSkyLightAt(x, z);
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

        return getBlock(x, y, z);
    }

    private synchronized void populateSkyLightAt(int x, int z) {
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
    public int getHighestLayer(int x, int z) {
        return height[(z << 4) + x];
    }

    @Override
    public byte getSkyLight(int x, int y, int z) {
        return sections[y / 16].getSkyLight(x, y % 16, z);
    }

    @Override
    public byte getBlockLight(int x, int y, int z) {
        return sections[y / 16].getBlockLight(x, y % 16, z);
    }

    @Override
    public ChunkSnapshot toSnapshot() {
        // TODO: Implement
        return null;
    }

    private static void checkPosition(int x, int y, int z) {
        Preconditions.checkArgument(x >= 0 && x <= 15, "x value (%s) not in range (0 to 15)", x);
        Preconditions.checkArgument(z >= 0 && z <= 15, "z value (%s) not in range (0 to 15)", z);
        Preconditions.checkArgument(y >= 0 && y < 128, "y value (%s) not in range (0 to 127)", y);
    }

    @Override
    public byte[] toFullChunkData() {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(96 * 1024); // Start at 96KB, more than enough to write the data out
        try {
            // Write the block IDs.
            for (int i = 0; i < sections.length; i++) {
                if (sections[i] != null) {
                    buf.writerIndex(5 * 2048 * i);
                    ChunkSection section = sections[i];
                    buf.writeBytes(section.getIds());
                    buf.writeBytes(section.getData().getData());
                    buf.writeBytes(section.getBlockLight().getData());
                    buf.writeBytes(section.getSkyLight().getData());
                }
            }

            buf.writerIndex(5 * 2048 * 8);
            buf.writeBytes(height);
            for (int i = 0; i < biomeColor.length; i++) {
                //int color = biomeColor[i];
                //byte biome = biomeId[i];
                //Varints.encodeSigned(buf, (color & 0x00ffffff) | biome << 2);
                buf.writeInt(biomeColor[i]);
            }
            // extra data, we have none
            buf.writeShort(0);

            try (NBTWriter writer = new NBTWriter(new ByteBufOutputStream(buf.order(ByteOrder.LITTLE_ENDIAN)), NBTEncoding.MCPE_0_16_NETWORK)) {
                if (serializedBlockEntities.isEmpty()) {
                    // Write out an empty root tag
                    writer.write(new CompoundTag("", ImmutableMap.of()));
                } else {
                    // Write out NBT compounds for all block entities.
                    for (CompoundTag entity : serializedBlockEntities.valueCollection()) {
                        writer.write(entity);
                    }
                }
            } catch (IOException e) {
                throw new AssertionError(e);
            }

            byte[] chunkData = new byte[buf.readableBytes()];
            buf.readBytes(chunkData);
            return chunkData;
        } finally {
            buf.release();
        }
    }
}
