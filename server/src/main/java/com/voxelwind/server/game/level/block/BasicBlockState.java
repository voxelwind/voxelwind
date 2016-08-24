package com.voxelwind.server.game.level.block;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.level.block.BlockData;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockType;

/**
 * This class implements a "simple" block state that only contains a block type.
 */
public class BasicBlockState implements BlockState {
    private final BlockType type;

    public BasicBlockState(BlockType type) {
        this.type = Preconditions.checkNotNull(type, "type");
    }

    @Override
    public BlockType getBlockType() {
        return type;
    }

    @Override
    public BlockData getBlockData() {
        // There isn't any
        return null;
    }
}
