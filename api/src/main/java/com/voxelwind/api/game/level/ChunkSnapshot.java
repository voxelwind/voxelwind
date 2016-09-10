package com.voxelwind.api.game.level;

import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockSnapshot;

import javax.annotation.concurrent.Immutable;

/**
 * A {@code ChunkSnapshot} is an immutable, level-independent snapshot of a {@link Chunk}.
 */
@Immutable
public interface ChunkSnapshot {
    int getX();

    int getZ();

    BlockSnapshot getBlock(int x, int y, int z);
}
