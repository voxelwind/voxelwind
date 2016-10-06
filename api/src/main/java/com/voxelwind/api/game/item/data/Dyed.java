package com.voxelwind.api.game.item.data;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.util.data.DyeColor;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Represents any dyed item, such as stained glass, wool, or dyes.
 */
public final class Dyed implements Metadata {
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
    public final DyeColor getColor() {
        return color;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dyed dyed = (Dyed) o;
        return color == dyed.color;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(color);
    }

    @Override
    public final String toString() {
        return "Dyed{" +
                "color=" + color +
                '}';
    }
}
