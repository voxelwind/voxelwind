package com.voxelwind.nbt.tags;

import java.util.Objects;

/**
 * Created by andrew on 10/21/16.
 */
public class DoubleTag implements Tag<Double> {
    private final String name;
    private final double value;

    public DoubleTag(String name, double value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Double getValue() {
        return value;
    }

    public double getPrimitiveValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleTag doubleTag = (DoubleTag) o;
        return Double.compare(doubleTag.value, value) == 0 &&
                Objects.equals(name, doubleTag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }

        return "TAG_Double" + append + ": " + value;
    }
}
