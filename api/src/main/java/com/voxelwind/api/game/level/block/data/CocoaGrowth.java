package com.voxelwind.api.game.level.block.data;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.level.block.BlockData;

public class CocoaGrowth implements BlockData {
    private final int level;

    public static final CocoaGrowth NEW = new CocoaGrowth(0);
    public static final CocoaGrowth FULLY_GROWN = new CocoaGrowth(8);

    public static CocoaGrowth ofStage(int data) {
        Preconditions.checkArgument(data >= 0 && data <= 8, "data is not valid (wanted 0-8)");
        return new CocoaGrowth(data);
    }

    private CocoaGrowth(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isFullyGrown() {
        return level == 8;
    }
}
