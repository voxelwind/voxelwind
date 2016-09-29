package com.voxelwind.api.game.item.data;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.util.DyeColor;

import javax.annotation.Nonnull;

/**
 * Represents any dyed item, such as stained glass, wool, or dyes.
 */
public class Dyed implements Metadata {
    @Nonnull
    public static Dyed of(@Nonnull DyeColor color) {
        Preconditions.checkNotNull(color, "color");
        return new Dyed(color);
    }

    private final DyeColor color;

    private Dyed(DyeColor color) {
        this.color = color;
    }

    @Nonnull
    public DyeColor getColor() {
        return color;
    }
}
