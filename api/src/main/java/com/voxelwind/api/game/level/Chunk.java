package com.voxelwind.api.game.level;

import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;

public interface Chunk {
    int getX();

    int getZ();

    Level getLevel();

    Block getBlock(int x, int y, int z);

    Block setBlock(int x, int y, int z, BlockState state);

    Block setBlock(int x, int y, int z, BlockState state, boolean shouldRecalculateLight);

    int getHighestLayer(int x, int z);

    byte getSkyLight(int x, int y, int z);

    byte getBlockLight(int x, int y, int z);

    ChunkSnapshot toSnapshot();
}
