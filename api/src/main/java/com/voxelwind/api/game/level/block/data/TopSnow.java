package com.voxelwind.api.game.level.block.data;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;

import java.util.Objects;

public class TopSnow implements Metadata {
    private final int layers;

    public static TopSnow of(int layer) {
        Preconditions.checkArgument(layer >= 0 && layer <= 7, "layers %s is not between 0 and 7", layer);
        return new TopSnow(layer);
    }

    private TopSnow(int layers) {
        this.layers = layers;
    }

    public int getLayers() {
        return layers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopSnow topSnow = (TopSnow) o;
        return layers == topSnow.layers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(layers);
    }

    @Override
    public String toString() {
        return "TopSnow{" +
                "layers=" + layers +
                '}';
    }
}
