package com.voxelwind.server.game.inventories;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.inventories.InventoryType;
import com.voxelwind.api.game.inventories.OpenableInventory;

import javax.annotation.Nonnull;

public class VoxelwindBaseOpenableInventory extends VoxelwindBaseInventory implements OpenableInventory {
    private final Vector3i position;

    protected VoxelwindBaseOpenableInventory(InventoryType type, Vector3i position) {
        super(type);
        this.position = position;
    }

    @Override
    @Nonnull
    public Vector3i getPosition() {
        return position;
    }
}
