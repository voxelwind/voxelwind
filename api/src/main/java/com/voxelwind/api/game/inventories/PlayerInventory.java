package com.voxelwind.api.game.inventories;

import com.voxelwind.api.game.item.ItemStack;

import java.util.Optional;

/**
 * Specifies a player inventory.
 */
public interface PlayerInventory extends Inventory {
    int[] getHotbarLinks();

    int getHeldSlot();

    Optional<ItemStack> getStackInHand();
}
