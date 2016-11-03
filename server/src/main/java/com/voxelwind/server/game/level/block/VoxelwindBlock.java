package com.voxelwind.server.game.level.block;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;

public class VoxelwindBlock implements Block {
    private final Level level;
    private final Chunk chunk;
    private final Vector3i location;
    private final BlockState state;

    public VoxelwindBlock(Level level, Chunk chunk, Vector3i location, BlockState state) {
        this.level = level;
        this.chunk = chunk;
        this.location = location;
        this.state = state;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public Chunk getChunk() {
        return chunk;
    }

    @Override
    public BlockState getBlockState() {
        return state;
    }

    @Override
    public Vector3i getLevelLocation() {
        return location;
    }
}
