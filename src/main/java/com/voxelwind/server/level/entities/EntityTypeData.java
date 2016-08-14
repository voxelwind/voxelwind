package com.voxelwind.server.level.entities;

public enum EntityTypeData {
    PLAYER(63, 1.62f),
    ZOMBIE(32, 1.62f);

    private final int type;
    private final float height;

    EntityTypeData(int type, float height) {
        this.type = type;
        this.height = height;
    }

    public int getType() {
        return type;
    }

    public float getHeight() {
        return height;
    }
}
