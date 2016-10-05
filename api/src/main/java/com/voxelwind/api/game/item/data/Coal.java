package com.voxelwind.api.game.item.data;

import com.voxelwind.api.game.Metadata;

import java.util.Objects;

/**
 * This {@link Metadata} represents coal. In Minecraft, coal can either be regular or be charcoal. They are the same,
 * regardless.
 */
public enum Coal implements Metadata {
    /**
     * Represents a regular coal item.
     */
    REGULAR,
    /**
     * Represents a charcoal item.
     */
    CHARCOAL;

    /**
     * Returns whether or not this item is charcoal.
     * @return whether or not this item is charcoal
     */
    public final boolean isCharcoal() {
        return this == CHARCOAL;
    }

    @Override
    public final String toString() {
        return "Coal{" + name() + '}';
    }
}
