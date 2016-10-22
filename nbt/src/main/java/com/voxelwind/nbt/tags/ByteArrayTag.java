package com.voxelwind.nbt.tags;

import java.util.Arrays;
import java.util.Objects;

public class ByteArrayTag implements Tag<byte[]> {
    private final String name;
    private final byte[] value;

    public ByteArrayTag(String name, byte[] value) {
        this.name = name;
        this.value = Objects.requireNonNull(value, "value").clone();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte[] getValue() {
        return value.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteArrayTag that = (ByteArrayTag) o;
        return Objects.equals(name, that.name) &&
                Arrays.equals(value, that.value);
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

        return "TAG_Byte_Array" + append + ": [" + value.length + " bytes]";
    }
}
