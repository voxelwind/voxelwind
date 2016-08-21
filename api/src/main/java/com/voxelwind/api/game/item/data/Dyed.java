package com.voxelwind.api.game.item.data;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.util.DyeColor;
import com.voxelwind.api.game.level.block.BlockData;

/**
 * Represents any dyed item, such as stained glass, wool, or dyes.
 */
public class Dyed implements MaterialData, BlockData {
    public static Dyed of(DyeColor color) {
        Preconditions.checkNotNull(color, "color");
        return new Dyed(color);
    }

    private final DyeColor color;

    private Dyed(DyeColor color) {
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }
}
