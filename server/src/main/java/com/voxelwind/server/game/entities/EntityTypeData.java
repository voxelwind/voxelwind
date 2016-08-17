package com.voxelwind.server.game.entities;

public enum EntityTypeData {
    PLAYER(63, 1.62f),
    
    CHICKEN(10, 0.7f),
    COW(11, 1.3f),
    PIG(12, 0.9f),
    SHEEP(13, 1.3f),
    WOLF(14, 0.8f),
    VILLAGER(15, 1.8f),
    MOOSHROOM(16, 1.3f),
    SQUID(17, 0.95f),
    RABBIT(18, 0f),
    BAT(19, 0.3f),
    IRON_GOLEM(20, 2.9f),
    SNOW_GOLEM(21, 1.9f),
    OCELOT(22, 0.7f),
    HORSE(23, 0),
    DONKEY(24, 0f),
    MULE(25, 0f),
    SKELETON_HORSE(26, 0f),
    ZOMBIE_HORSE(27, 0f),
        
    ZOMBIE(32, 1.8f),
    CREEPER(33, 1.8f),
    SKELETON(34, 1.8f),
    SPIDER(35, 1.12f),
    ZOMBIE_PIGMAN(36, 1.8f),
    SLIME(37, 0),
    ENDERMAN(38, 2.9f),
    SILVERFISH(39, 0.3f),
    CAVE_SPIDER(40, 0.5f),
    GHAST(41, 4.0f),
    MAGMA_CUBE(42, 0f),
    BLAZE(43, 1.8f),
    ZOMBIE_VILLAGER(44, 1.8f),
    WITCH(45, 1.8f),
    STRAY(46, 1.8f),
    HUSK(47, 1.8f),
    WITHER_SKELETON(48, 3.5f);
    
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
