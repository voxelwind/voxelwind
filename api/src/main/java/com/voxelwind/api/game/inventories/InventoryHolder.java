package com.voxelwind.api.game.inventories;

/**
 * Specifies an inventory holder.
 */
public interface InventoryHolder {
    /**
     * Returns this instance's inventory.
     * @return an inventory
     */
    Inventory getInventory();
}
