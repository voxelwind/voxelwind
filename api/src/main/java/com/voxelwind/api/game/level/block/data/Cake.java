package com.voxelwind.api.game.level.block.data;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.level.block.BlockData;

/**
 * Represents a block of cake.
 */
public class Cake implements BlockData {
    private final int level;

    public static final Cake NEW = new Cake(0);
    public static final Cake ALMOST_EATEN = new Cake(6);

    public static Cake ofStage(int data) {
        Preconditions.checkArgument(data >= 0 && data < 7, "data is not valid (wanted 0-6)");
        return new Cake(data);
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

    @Override
    public short toBlockMetadata() {
        return (short) level;
    }
}
