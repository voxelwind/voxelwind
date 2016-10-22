package com.voxelwind.nbt.tags;

import java.util.Objects;

/**
 * Created by andrew on 10/21/16.
 */
public class LongTag implements Tag<Long> {
    private final String name;
    private final long value;

    public LongTag(String name, long value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Long getValue() {
        return value;
    }

    public long getPrimitiveValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongTag longTag = (LongTag) o;
        return value == longTag.value &&
                Objects.equals(name, longTag.name);
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

        return "TAG_Long" + append + ": " + value;
    }
}
