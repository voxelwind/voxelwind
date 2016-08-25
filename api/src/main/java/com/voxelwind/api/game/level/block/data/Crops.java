package com.voxelwind.api.game.level.block.data;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.level.block.BlockData;

public class Crops implements BlockData {
    private final int level;

    public static final Crops NEW = new Crops(0);
    public static final Crops FULLY_GROWN = new Crops(7);

    public static Crops ofStage(int data) {
        Preconditions.checkArgument(data >= 0 && data < 8, "data is not valid (wanted 0-7)");
        return new Crops(data);
    }

    private Crops(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isFullyGrown() {
        return level == 8;
    }
}
