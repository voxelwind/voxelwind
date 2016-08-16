package com.voxelwind.server.game.item;

public enum ItemType {
    STONE(1);

    private final short id;

    ItemType(short id) {
        this.id = id;
    }

    ItemType(int i) {
        this.id = (short) i;
    }
}
