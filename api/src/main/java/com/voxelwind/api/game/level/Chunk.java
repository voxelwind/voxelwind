package com.voxelwind.api.game.level;

import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;

public interface Chunk {
    int getX();

    int getZ();

    Level getLevel();

    Block getBlock(int x, int y, int z);

    Block setBlock(int x, int y, int z, BlockState state);

    ChunkSnapshot toSnapshot();
}
