package com.voxelwind.server.game.inventories;

import com.voxelwind.api.game.inventories.Inventory;
import com.voxelwind.api.game.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;

public interface InventoryObserver {
    void onInventoryChange(int slot, @Nullable ItemStack oldItem, @Nullable ItemStack newItem, Inventory inventory);
    void onInventoryContentsReplacement(Map<Integer, ItemStack> newItems, Inventory inventory);
}