package com.voxelwind.api.game.level;

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

    int getHighestLayer(int x, int z);

    byte getSkyLight(int x, int y, int z);

    byte getBlockLight(int x, int y, int z);
}
