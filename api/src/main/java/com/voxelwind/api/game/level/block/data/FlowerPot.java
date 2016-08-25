package com.voxelwind.api.game.level.block.data;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.level.block.BlockData;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Represents all the states of a flower pot.
 */
public class FlowerPot implements BlockData {
    public static FlowerPot of(Type type) {
        Preconditions.checkNotNull(type, "type");
        return new FlowerPot(type);
    }

    private final Type type;

    public FlowerPot(Type type) {
        this.type = type;
    }

    @Nonnull
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowerPot flowerPot = (FlowerPot) o;
        return type == flowerPot.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return "FlowerPot{" +
                "type=" + type +
                '}';
    }

    public enum Type {
        EMPTY,
        POPPY,
        DANDELION,
        OAK_SAPLING,
        SPRUCE_SAPLING,
        BIRCH_SAPLING,
        JUNGLE_SAPLING,
        RED_MUSHROOM,
        CACTUS,
        DEAD_BUSH,
        FERN,
        ACACIA_SAPLING,
        DARK_OAK_SAPLING
    }
}
