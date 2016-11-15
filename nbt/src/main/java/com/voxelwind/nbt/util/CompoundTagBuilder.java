package com.voxelwind.nbt.util;

import com.voxelwind.nbt.tags.CompoundTag;
import com.voxelwind.nbt.tags.Tag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompoundTagBuilder {
    private final Map<String, Tag<?>> tagMap = new HashMap<>();

    public static CompoundTagBuilder create() {
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

    public CompoundTag buildRootTag() {
        return new CompoundTag("", tagMap);
    }

    public CompoundTag build(String tagName) {
        return new CompoundTag(tagName, tagMap);
    }
}
