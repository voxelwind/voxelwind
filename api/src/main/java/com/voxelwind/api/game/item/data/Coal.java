package com.voxelwind.api.game.item.data;

import com.voxelwind.api.game.Metadata;

import java.util.Objects;

/**
 * This {@link Metadata} represents coal. In Minecraft, coal can either be regular or be charcoal. They are the same,
 * regardless.
 */
public final class Coal implements Metadata {
    /**
     * Represents a regular coal item.
     */
    public static final Coal REGULAR = new Coal(false);
    /**
     * Represents a charcoal item.
     */
    public static final Coal CHARCOAL = new Coal(true);

    private final boolean isCharcoal;

    private Coal(boolean isCharcoal) {
        this.isCharcoal = isCharcoal;
    }

    /**
     * Returns whether or not this item is charcoal.
     * @return whether or not this item is charcoal
     */
    public final boolean isCharcoal() {
        return isCharcoal;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coal coal = (Coal) o;
        return isCharcoal == coal.isCharcoal;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(isCharcoal);
    }

    @Override
    public final String toString() {
        return "Coal{" +
                "isCharcoal=" + isCharcoal +
                '}';
    }
}
