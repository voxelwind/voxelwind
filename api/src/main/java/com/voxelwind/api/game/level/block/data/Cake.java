package com.voxelwind.api.game.level.block.data;

import com.voxelwind.api.game.Metadata;

/**
 * Represents a block of cake.
 */
public class Cake implements Metadata {
    private final int level;

    public static final Cake NEW = new Cake(0);
    public static final Cake ALMOST_EATEN = new Cake(6);

    public static Cake of(short meta) {
        return new Cake(meta);
    }

    private Cake(int level) {
        this.level = level;
    }

    /**
     * Returns the current amount of slices eaten.
     * @return the slices eaten
     */
    public int getSlicesEaten() {
        return level;
    }

    /**
     * Returns the amount of slices left.
     * @return the slices left
     */
    public int getSlicesLeft() {
        return 7 - level;
    }
}
