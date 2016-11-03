package com.voxelwind.api.game.level.block;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;

/**
 * This class represents a block that has been placed into the world.
 */
public interface Block extends BlockSnapshot {
    Level getLevel();
    Chunk getChunk();
    Vector3i getLevelLocation();

    default Vector3i getChunkLocation() {
        Vector3i level = getLevelLocation();
        return new Vector3i(level.getX() & 0x0f, level.getY(), level.getZ() & 0x0f);
    }
}
