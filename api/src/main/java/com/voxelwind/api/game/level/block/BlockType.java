package com.voxelwind.api.game.level.block;

import com.voxelwind.api.game.item.ItemType;

/**
 * This interface specifies a kind of block.
 */
public interface BlockType extends ItemType {
    default boolean isBlock() {
        return true;
    }

    boolean isDiggable();
    boolean isTransparent();
    boolean isFlammable();

    int emitsLight();
    int filtersLight();
}
