package com.voxelwind.api.game.level.block;

import javax.annotation.concurrent.Immutable;

@Immutable
public interface BlockSnapshot {
    BlockState getBlockState();
}
