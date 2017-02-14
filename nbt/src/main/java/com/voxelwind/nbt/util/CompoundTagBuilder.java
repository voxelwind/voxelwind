package com.voxelwind.nbt.util;

import com.voxelwind.nbt.tags.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompoundTagBuilder {
    private final Map<String, Tag<?>> tagMap = new HashMap<>();

    public static CompoundTagBuilder builder() {
        return new CompoundTagBuilder();
    }

    public static CompoundTagBuilder from(CompoundTag tag) {
        CompoundTagBuilder builder = new CompoundTagBuilder();
        builder.tagMap.putAll(tag.getValue());
        return builder;
    }

    public CompoundTagBuilder tag(Tag<?> tag) {
        tagMap.put(tag.getName(), tag);
        return this;
    }

    public CompoundTagBuilder tag(String name, byte value) {
        return tag(new ByteTag(name, value));
    }

    public CompoundTagBuilder tag(String name, byte [] value) {
        return tag(new ByteArrayTag(name, value));
    }

    public CompoundTagBuilder tag(String name, double value) {
        return tag(new DoubleTag(name, value));
    }

    public CompoundTagBuilder tag(String name, float value) {
        return tag(new FloatTag(name, value));
    }

    public CompoundTagBuilder tag(String name, int[] value) {
        return tag(new IntArrayTag(name, value));
    }

    public CompoundTagBuilder tag(String name, int value) {
        return tag(new IntTag(name, value));
    }

    public CompoundTagBuilder tag(String name, long value) {
        return tag(new LongTag(name, value));
    }

    public CompoundTagBuilder tag(String name, short value) {
        return tag(new ShortTag(name, value));
    }

    public CompoundTagBuilder tag(String name, String value) {
        return tag(new StringTag(name, value));
    }

    public CompoundTag buildRootTag() {
        return new CompoundTag("", tagMap);
    }

    public CompoundTag build(String tagName) {
        return new CompoundTag(tagName, tagMap);
    }
}
