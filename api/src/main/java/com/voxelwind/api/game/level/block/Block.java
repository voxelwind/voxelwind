package com.voxelwind.api.game.level.block;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.blockentities.BlockEntity;

import java.util.Optional;

/**
 * This class represents a block that has been placed into the world.
 */
public interface Block {
    Level getLevel();
    Chunk getChunk();
    BlockState getBlockState();
    Vector3i getLevelLocation();
    Optional<BlockEntity> getBlockEntity();

    default Vector3i getChunkLocation() {
        Vector3i level = getLevelLocation();
        return new Vector3i(level.getX() % 16, level.getY(), level.getZ() % 16);
    }
}
