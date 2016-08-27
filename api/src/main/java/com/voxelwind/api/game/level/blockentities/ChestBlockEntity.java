package com.voxelwind.api.game.level.blockentities;

import com.voxelwind.api.game.inventories.InventoryHolder;
import com.voxelwind.api.game.inventories.OpenableInventory;

/**
 * Represents a block entity associated with a chest.
 */
public interface ChestBlockEntity extends BlockEntity, InventoryHolder {
    @Override
    OpenableInventory getInventory();
}