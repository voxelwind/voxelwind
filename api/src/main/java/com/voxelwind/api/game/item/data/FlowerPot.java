package com.voxelwind.api.game.item.data;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.level.block.BlockData;

/**
 * Represents all the states of a flower pot.
 */
public class FlowerPot implements MaterialData, BlockData {
    public static FlowerPot of(Type type) {
        Preconditions.checkNotNull(type, "type");
        return new FlowerPot(type);
    }

    private final Type type;

    public FlowerPot(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
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
