package com.voxelwind.api.game.item.data;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.util.data.BlockFace;

import java.util.Objects;

public class Directional implements Metadata {
    private final BlockFace face;

    public Directional(BlockFace face) {
        this.face = face;
    }

    public static Directional of(BlockFace face) {
        Preconditions.checkNotNull(face, "face");
        return new Directional(face);
    }

    public BlockFace getFace() {
        return face;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Directional that = (Directional) o;
        return face == that.face;
    }

    @Override
    public int hashCode() {
        return Objects.hash(face);
    }

    @Override
    public String toString() {
        return "Directional{" +
                "face=" + face +
                '}';
    }
}
