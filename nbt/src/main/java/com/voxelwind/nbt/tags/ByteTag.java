package com.voxelwind.nbt.tags;

import java.util.Objects;

/**
 * Represents a named tag with a byte value.
 */
public class ByteTag implements Tag<Byte> {
    private final String name;
    private final byte value;

    public ByteTag(String name, byte value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Byte getValue() {
        return value;
    }

    public byte getPrimitiveValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteTag byteTag = (ByteTag) o;
        return value == byteTag.value &&
                Objects.equals(name, byteTag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        String append = name == null ? "" : "('" + name + "')";
        return "TAG_Byte" + append + ": " + value;
    }
}
