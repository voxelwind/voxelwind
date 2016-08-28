package com.voxelwind.server.game.inventories;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.inventories.InventoryType;
import com.voxelwind.server.network.session.PlayerSession;

public enum VoxelwindInventoryType {
    CHEST((byte) 0, (byte) 10),
    DOUBLE_CHEST((byte) 0, (byte) 10),
    FURNACE((byte) 2, (byte) 11),
    ENCHANTING((byte) 4, (byte) 12),
    ANVIL((byte) 5),
    PLAYER((byte) -1, (byte) 0), // Fake type, can't be opened.
    CRAFTING((byte) 1),
    WORKBENCH((byte) 1),
    BREWING_STAND((byte) 4),
    DISPENSER((byte) 6),
    DROPPER((byte) 7),
    HOPPER((byte) 8);

    private final byte id;
    private final byte type;

    VoxelwindInventoryType(byte type, byte id) {
        this.id = id;
        this.type = type;
    }

    VoxelwindInventoryType(byte type) {
        this(type, (byte) -1);
    }

    public byte getWindowId(PlayerSession session) {
        return id == -1 ? session.getNextWindowId() : id;
    }

    public byte getType() {
        return type;
    }

    public static VoxelwindInventoryType fromApi(InventoryType type) {
        Preconditions.checkNotNull(type, "type");
        return valueOf(type.name());
    }
}
