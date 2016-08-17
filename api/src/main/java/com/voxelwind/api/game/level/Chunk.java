package com.voxelwind.api.game.level;

import com.voxelwind.api.game.level.block.BlockState;

public interface Chunk {
    int getX();

    int getZ();

    BlockState getBlock(int x, int y, int z);

    void setType(int x, int y, int z, BlockState type);
}
