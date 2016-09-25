package com.voxelwind.server.game.level.provider.anvil;

import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.nbt.*;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.ChunkSnapshot;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.*;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.block.VoxelwindBlock;
import com.voxelwind.server.game.level.util.NibbleArray;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Optional;

public class AnvilChunk implements Chunk {
    private final int x;
    private final int z;
    private final Level level;
    private final CompoundMap levelData;
    private final TIntObjectHashMap<CompoundMap> sections;

    public AnvilChunk(int x, int z, Level level, CompoundMap levelData) {
        this.x = x;
        this.z = z;
        this.level = level;
        this.levelData = levelData;
        this.sections = generateMap(levelData);
    }

    private static TIntObjectHashMap<CompoundMap> generateMap(CompoundMap levelData) {
        ListTag<CompoundTag> sectionsList = (ListTag<CompoundTag>) levelData.get("Sections");
        TIntObjectHashMap<CompoundMap> map = new TIntObjectHashMap<CompoundMap>();
        for (CompoundTag tag : sectionsList.getValue()) {
            int y = ((IntTag) tag.getValue().get("Y")).getValue();
            map.put(y, tag.getValue());
        }
        return map;
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
        Vector3i full = new Vector3i(x + (this.x * 16), y, z + (this.z * 16));
        CompoundMap map = sections.get(y / 16);
        if (map == null) {
            return new VoxelwindBlock(level, this, full, new BasicBlockState(BlockTypes.AIR, null), null);
        }

        byte[] blockIds = ((ByteArrayTag) map.get("Blocks")).getValue();
        NibbleArray data = new NibbleArray(((ByteArrayTag) map.get("Data")).getValue());
        // Anvil supports IDs above 256, however MCPE does not currently do so.
        int pos = blockPosition(x, y % 16, z);
        BlockType type = BlockTypes.forId(blockIds[pos]);

        // TODO: Add block data, entities, other things.
        Optional<BlockData> createdData = type.createBlockDataFor(data.get(pos));
        return new VoxelwindBlock(level, this, full, new BasicBlockState(type, createdData.orElse(null)), null);
    }

    @Override
    public Block setBlock(int x, int y, int z, BlockState state) {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    @Override
    public ChunkSnapshot toSnapshot() {
        return null;
    }

    private static void checkPosition(int x, int y, int z) {
        Preconditions.checkArgument(x >= 0 && x <= 15, "x value (%s) not in range (0 to 15)", x);
        Preconditions.checkArgument(z >= 0 && z <= 15, "z value (%s) not in range (0 to 15)", z);
        Preconditions.checkArgument(y >= 0 && y <= 128, "y value (%s) not in range (0 to 128)", y);
    }

    private static int blockPosition(int x, int y, int z) {
        return y * 16 * 16 + z * 16 + x;
    }
}
