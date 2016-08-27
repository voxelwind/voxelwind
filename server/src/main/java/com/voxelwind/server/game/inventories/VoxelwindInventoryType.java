package com.voxelwind.server.game.inventories;

import com.voxelwind.server.network.session.PlayerSession;

public enum VoxelwindInventoryType {
    CHEST(10),
    FURNACE(11),
    ENCHANTING(12),
    ANVIL,
    PLAYER(0),
    PLAYER_ARMOR(0x78);

    private int id = -1;

    VoxelwindInventoryType(int id) {
        this.id = id;
    }

    VoxelwindInventoryType() {

    }

    public int getWindowId(PlayerSession session) {
        return id == -1 ? session.getNextWindowId() : id;
    }
}
