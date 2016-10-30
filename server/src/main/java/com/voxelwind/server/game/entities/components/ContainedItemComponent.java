package com.voxelwind.server.game.entities.components;

import com.voxelwind.api.game.entities.components.ContainedItem;
import com.voxelwind.api.game.item.ItemStack;

public class ContainedItemComponent implements ContainedItem {
    private final ItemStack stack;

    public ContainedItemComponent(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack getItemStack() {
        return stack;
    }
}
