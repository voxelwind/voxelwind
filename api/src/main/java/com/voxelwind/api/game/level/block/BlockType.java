package com.voxelwind.api.game.level.block;

import com.voxelwind.api.game.item.Material;

/**
 * This interface specifies a kind of block.
 */
public interface BlockType extends Material {
    default boolean isBlock() {
        return true;
    }
}
