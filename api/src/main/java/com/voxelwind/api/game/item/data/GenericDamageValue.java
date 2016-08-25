package com.voxelwind.api.game.item.data;

/**
 * This class provides a generic damage value.
 */
public class GenericDamageValue implements ItemData {
    private final short damage;

    public GenericDamageValue(short damage) {
        this.damage = damage;
    }

    @Override
    public short toMetadata() {
        return damage;
    }
}
