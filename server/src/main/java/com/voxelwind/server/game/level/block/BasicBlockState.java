package com.voxelwind.server.game.level.block;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.blockentities.BlockEntity;

import java.util.Optional;

/**
 * This class implements a "simple" block state that only contains a block type.
 */
public class BasicBlockState implements BlockState {
    private final BlockType type;
    private final Metadata data;
    private final BlockEntity blockEntity;

    public BasicBlockState(BlockType type, Metadata data, BlockEntity blockEntity) {
        this.type = Preconditions.checkNotNull(type, "type");
        this.data = data;
        this.blockEntity = blockEntity;
    }

    @Override
    public BlockType getBlockType() {
        return type;
    }

    @Override
    public Metadata getBlockData() {
        return data;
    }

    @Override
    public Optional<BlockEntity> getBlockEntity() {
        return Optional.ofNullable(blockEntity);
    }
}
