package com.voxelwind.nbt.tags;

import java.util.Objects;

public class FloatTag implements Tag<Float> {
    private final String name;
    private final float value;

    public FloatTag(String name, float value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Float getValue() {
        return value;
    }

    public float getPrimitiveValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloatTag floatTag = (FloatTag) o;
        return Float.compare(floatTag.value, value) == 0 &&
                Objects.equals(name, floatTag.name);
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

        return "TAG_Float" + append + ": " + value;
    }
}
