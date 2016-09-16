package com.voxelwind.api.game.level.block.data;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.level.block.BlockData;

import java.util.Objects;

public class TopSnow implements BlockData {
    private final int layer;

    public static TopSnow from(int layer) {
        Preconditions.checkArgument(layer >= 0 && layer <= 7, "layer %s is not between 0 and 7", layer);
        return new TopSnow(layer);
    }

    private TopSnow(int layer) {
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
    }

    @Override
    public short toBlockMetadata() {
        return (short) layer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopSnow topSnow = (TopSnow) o;
        return layer == topSnow.layer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(layer);
    }

    @Override
    public String toString() {
        return "TopSnow{" +
                "layer=" + layer +
                '}';
    }
}
