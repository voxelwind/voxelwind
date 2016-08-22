package com.voxelwind.api.game.item.data;

/**
 * This {@link ItemData} represents coal. In Minecraft, coal can either be regular or be charcoal. They are the same,
 * regardless.
 */
public class Coal implements ItemData {
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
    public boolean isCharcoal() {
        return isCharcoal;
    }
}
