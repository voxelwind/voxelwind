package com.voxelwind.server.game.level.chunk;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.ChunkSnapshot;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.BlockSnapshot;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.block.VoxelwindBlock;
import com.voxelwind.server.game.serializer.MetadataSerializer;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Optional;

public class SectionedChunkSnapshot implements ChunkSnapshot {
    protected final ChunkSection[] sections;
    protected final int x;
    protected final int z;
    protected final byte[] biomeId = new byte[256];
    protected final byte[] height = new byte[512];
    protected final TIntObjectMap<BlockEntity> blockEntities = new TIntObjectHashMap<>();

    public SectionedChunkSnapshot(ChunkSection[] sections, int x, int z) {
        this.sections = sections;
        this.x = x;
        this.z = z;
    }

    static int xyzIdx(int x, int y, int z) {
        return (x * 256) + (z * 16) + y;
    }

    static void checkPosition(int x, int y, int z) {
        Preconditions.checkArgument(x >= 0 && x <= 15, "x value (%s) not in range (0 to 15)", x);
        Preconditions.checkArgument(z >= 0 && z <= 15, "z value (%s) not in range (0 to 15)", z);
        Preconditions.checkArgument(y >= 0 && y < 128, "y value (%s) not in range (0 to 127)", y);
    }

    Vector3i getLevelLocation(int chunkX, int y, int chunkZ) {
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
    public BlockSnapshot getBlock(int x, int y, int z) {
        return getBlock(null, null, x, y, z);
    }

    BlockSnapshot getBlock(Level level, Chunk chunk, int x, int y, int z) {
        checkPosition(x, y, z);
        ChunkSection section = sections[y / 16];
        Vector3i full = getLevelLocation(x, y, z);

        if (section == null) {
            return new VoxelwindBlock(level, chunk, full, new BasicBlockState(BlockTypes.AIR, null, null));
        }

        BlockType type = BlockTypes.forId(section.getBlockId(x, y % 16, z));
        Optional<Metadata> createdData;
        if (type.getMetadataClass() != null) {
            createdData = Optional.of(MetadataSerializer.deserializeMetadata(type, section.getBlockData(x, y % 16, z)));
        } else {
            createdData = Optional.empty();
        }

        return new VoxelwindBlock(level, chunk, full, new BasicBlockState(type, createdData.orElse(null), blockEntities.get(xyzIdx(x, y, z))));
    }

    @Override
    public int getHighestLayer(int x, int z) {
        return height[(z << 4) + x];
    }

    @Override
    public byte getSkyLight(int x, int y, int z) {
        ChunkSection section = sections[y / 16];
        if (section == null) {
            return 15;
        }
        return section.getSkyLight(x, y % 16, z);
    }

    @Override
    public byte getBlockLight(int x, int y, int z) {
        ChunkSection section = sections[y / 16];
        if (section == null) {
            return 15;
        }
        return section.getBlockLight(x, y % 16, z);
    }
}
