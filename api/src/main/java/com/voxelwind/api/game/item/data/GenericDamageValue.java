package com.voxelwind.api.game.item.data;

import com.voxelwind.api.game.Metadata;

/**
 * This class provides a generic damage value.
 */
public final class GenericDamageValue implements Metadata {
    private final short damage;

    public GenericDamageValue(short damage) {
        this.damage = damage;
    }

    public final short getDamage() {
        return damage;
    }
}
