package com.voxelwind.server.game.level.blockentities;

import com.voxelwind.api.game.inventories.OpenableInventory;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.blockentities.ChestBlockEntity;

public class VoxelwindChestBlockEntity implements ChestBlockEntity {
    private final OpenableInventory inventory;

    public VoxelwindChestBlockEntity(OpenableInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public OpenableInventory getInventory() {
        return inventory;
    }
}
