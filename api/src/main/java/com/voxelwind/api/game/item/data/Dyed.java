package com.voxelwind.api.game.item.data;

import com.google.common.base.Preconditions;
import com.voxelwind.api.util.DyeColor;
import com.voxelwind.api.game.level.block.BlockData;

import javax.annotation.Nonnull;

/**
 * Represents any dyed item, such as stained glass, wool, or dyes.
 */
public class Dyed implements ItemData, BlockData {
    @Nonnull
    public static Dyed of(@Nonnull DyeColor color) {
        Preconditions.checkNotNull(color, "color");
        return new Dyed(color);
    }

    @Nonnull
    public static Dyed of(short color) {
        DyeColor[] colors = DyeColor.values();
        Preconditions.checkArgument(color >= 0 && color < colors.length, "color is not valid");
        return new Dyed(colors[color]);
    }

    private final DyeColor color;

    private Dyed(DyeColor color) {
        this.color = color;
    }

    @Nonnull
    public DyeColor getColor() {
        return color;
    }

    @Override
    public short toMetadata() {
        return (short) color.ordinal();
    }
}
