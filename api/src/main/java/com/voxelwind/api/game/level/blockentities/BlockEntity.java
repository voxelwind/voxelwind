package com.voxelwind.api.game.level.blockentities;

import com.voxelwind.api.game.level.block.Block;

/**
 * This interface represents a block entity.
 */
public interface BlockEntity {
    /**
     * Returns the block associated with this entity.
     * @return a block
     */
    Block getBlock();
}
