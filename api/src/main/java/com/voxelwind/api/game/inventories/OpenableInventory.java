package com.voxelwind.api.game.inventories;

import com.flowpowered.math.vector.Vector3i;

import javax.annotation.Nonnull;

/**
 * Indicates that an inventory that can be opened.
 */
public interface OpenableInventory extends Inventory {
    /**
     * Returns the position for this inventory.
     * @return the position
     */
    @Nonnull
    Vector3i getPosition();
}
