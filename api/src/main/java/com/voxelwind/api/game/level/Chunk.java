package com.voxelwind.api.game.level;

import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;

public interface Chunk {
    int getX();

    int getZ();

    Block getBlock(int x, int y, int z);

    Block setType(int x, int y, int z, BlockState state);
}
