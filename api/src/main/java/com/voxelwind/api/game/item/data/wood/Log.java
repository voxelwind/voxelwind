package com.voxelwind.api.game.item.data.wood;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.util.data.LogDirection;
import com.voxelwind.api.game.util.data.TreeSpecies;

import java.util.Objects;

/**
 * Represents a log.
 */
public class Log extends Wood {
    public static Log of(TreeSpecies species, LogDirection direction) {
        Preconditions.checkNotNull(species, "species");
        Preconditions.checkNotNull(direction, "direction");
        return new Log(species, direction);
    }

    private final LogDirection direction;

    private Log(TreeSpecies species, LogDirection direction) {
        super(species);
        this.direction = direction;
    }

    public LogDirection getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Log log = (Log) o;
        return direction == log.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), direction);
    }

    @Override
    public String toString() {
        return "Log{" +
                "species=" + getDirection() + ',' +
                "direction=" + direction +
                '}';
    }
}
