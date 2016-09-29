package com.voxelwind.api.game.level.block.data;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;

import java.util.Objects;

/**
 * Represents a crop. This includes wheat, melon and pumpkin stems, potatoes and carrots.
 */
public class Crops implements Metadata {
    private final int level;

    public static final Crops NEW = new Crops(0);
    public static final Crops FULLY_GROWN = new Crops(7);

    public static Crops of(int data) {
        Preconditions.checkArgument(data >= 0 && data < 8, "data is not valid (wanted 0-7)");
        return new Crops(data);
    }

    private Crops(int level) {
        this.level = level;
    }

    /**
     * Returns the current growth level.
     * @return the growth level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Determines if the crop has reached its last stage of growth.
     * @return if {@code level} is 0x7
     */
    public boolean isFullyGrown() {
        return level == 7;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crops crops = (Crops) o;
        return level == crops.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(level);
    }

    @Override
    public String toString() {
        return "Crops{" +
                "level=" + level +
                ", fullyGrown=" + isFullyGrown() +
                '}';
    }
}
