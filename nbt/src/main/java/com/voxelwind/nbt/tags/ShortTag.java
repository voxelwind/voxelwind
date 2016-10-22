package com.voxelwind.nbt.tags;

import java.util.Objects;

/**
 * Represents a named tag with a short value.
 */
public class ShortTag implements Tag<Short> {
    private final String name;
    private final short value;

    public ShortTag(String name, short value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Short getValue() {
        return value;
    }

    public short getPrimitiveValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortTag byteTag = (ShortTag) o;
        return value == byteTag.value &&
                Objects.equals(name, byteTag.name);
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

        return "TAG_Short" + append + ": " + value;
    }
}