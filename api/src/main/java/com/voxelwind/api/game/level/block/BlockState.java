package com.voxelwind.api.game.level.block;

import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.level.blockentities.BlockEntity;

import java.util.Optional;

/**
 * This class represents a block that is not necessarily placed into the world.
 */
public interface BlockState {
    /**
     * Returns the type of block.
     * @return the type for this block
     */
    BlockType getBlockType();

    /**
     * Returns the block data.
     * @return block data
     */
    Metadata getBlockData();

    /**
     * Returns the block entity associated with this block state.
     * @return the block entity to use
     */
    Optional<BlockEntity> getBlockEntity();
}
