package com.voxelwind.server.game.level.blockentities;

import com.voxelwind.api.game.inventories.OpenableInventory;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.blockentities.ChestBlockEntity;

class VoxelwindChestBlockEntity implements ChestBlockEntity {
    private final Block block;
    private final OpenableInventory inventory;

    public VoxelwindChestBlockEntity(Block block, OpenableInventory inventory) {
        this.block = block;
        this.inventory = inventory;
    }

    @Override
    public OpenableInventory getInventory() {
        return inventory;
    }

    @Override
    public Block getBlock() {
        return block;
    }
}
