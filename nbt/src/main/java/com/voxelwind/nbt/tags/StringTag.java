package com.voxelwind.nbt.tags;

import java.util.Objects;

public class StringTag implements Tag<String> {
    private final String name;
    private final String value;

    public StringTag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringTag stringTag = (StringTag) o;
        return Objects.equals(name, stringTag.name) &&
                Objects.equals(value, stringTag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        String append = name == null ? "" : "('" + name + "')";
        return "TAG_String" + append + ": " + value;
    }
}
