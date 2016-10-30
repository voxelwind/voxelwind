package com.voxelwind.api.game.entities.components;

import com.voxelwind.api.game.item.ItemStack;

/**
 * A {@link Component} that holds a single {@link ItemStack}.
 */
public interface ContainedItem {
    /**
     * Returns the contained item stack.
     * @return the item stack contained
     */
    ItemStack getItemStack();
}
