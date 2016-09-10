package com.voxelwind.api.game.level.block;

import com.voxelwind.api.game.level.blockentities.BlockEntity;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
public interface BlockSnapshot {
    BlockState getBlockState();
    Optional<BlockEntity> getBlockEntity();
}
