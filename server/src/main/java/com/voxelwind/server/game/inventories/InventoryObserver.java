package com.voxelwind.server.game.inventories;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.server.network.session.PlayerSession;

import javax.annotation.Nullable;

public interface InventoryObserver {
    void onInventoryChange(int slot, @Nullable ItemStack oldItem, @Nullable ItemStack newItem, VoxelwindBaseInventory inventory, @Nullable PlayerSession cause);
    void onInventoryContentsReplacement(ItemStack[] newItems, VoxelwindBaseInventory inventory);
}