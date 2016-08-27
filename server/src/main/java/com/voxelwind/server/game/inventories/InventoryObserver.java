package com.voxelwind.server.game.inventories;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.server.Player;

import javax.annotation.Nullable;
import java.util.Map;

public interface InventoryObserver {
    void onInventoryChange(int slot, ItemStack oldItem, @Nullable ItemStack newItem, @Nullable Player cause);
    void onInventoryContentsReplacement(Map<Integer, ItemStack> newItems);
}