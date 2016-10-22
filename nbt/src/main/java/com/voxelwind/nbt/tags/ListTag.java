package com.voxelwind.nbt.tags;

import com.voxelwind.nbt.io.TagType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ListTag<T extends Tag> implements Tag<List<T>> {
    private final String name;
    private final Class<T> tagClass;
    private final List<T> value;

    public ListTag(String name, Class<T> tagClass, List<T> value) {
        this.name = name;
        this.value = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(value, "value")));
        this.tagClass = tagClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<T> getValue() {
        return value;
    }

    public Class<T> getTagClass() {
        return tagClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListTag<?> listTag = (ListTag<?>) o;
        return Objects.equals(name, listTag.name) &&
                Objects.equals(tagClass, listTag.tagClass) &&
                Objects.equals(value, listTag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tagClass, value);
    }

    @Override
    public String toString() {
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }

        StringBuilder bldr = new StringBuilder();
        bldr.append("TAG_List").append(append).append(": ").append(value.size()).append(" entries of type ").append(TagType.fromClass(tagClass).getTypeName()).append("\r\n{\r\n");
        for (Tag t : value) {
            bldr.append("   ").append(t.toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
        }
        bldr.append("}");
        return bldr.toString();
    }
}
