package com.voxelwind.api.game.level.block;

/**
 * This class represents a block that is not necessarily placed into the world.
 */
public interface BlockState {
    /**
     * Returns the type of block.
     * @return the type for this block
     */
    BlockType getBlockType();
}
