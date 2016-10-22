package com.voxelwind.nbt.io;

import com.voxelwind.nbt.tags.*;

import java.util.HashMap;
import java.util.Map;

public enum TagType {
    END(EndTag.class),
    BYTE(ByteTag.class),
    SHORT(ShortTag.class),
    INT(IntTag.class),
    LONG(LongTag.class),
    FLOAT(FloatTag.class),
    DOUBLE(DoubleTag.class),
    BYTE_ARRAY(ByteArrayTag.class),
    STRING(StringTag.class),
    LIST(ListTag.class),
    COMPOUND(CompoundTag.class),
    INT_ARRAY(IntArrayTag.class);

    private final Class<? extends Tag> tagClass;

    private static final TagType[] VALUES = values();
    private static final Map<Class<?>, TagType> tagTypes = new HashMap<>();

    TagType(Class<? extends Tag> tagClass) {
        this.tagClass = tagClass;
    }

    public Class<? extends Tag> getTagClass() {
        return tagClass;
    }

    public String getTypeName() {
        return "TAG_" + name();
    }

    public static TagType fromId(int id) {
        if (id < 0 || id >= VALUES.length) {
            return null;
        }

        return VALUES[id];
    }

    public static TagType fromClass(Class<? extends Tag> tagClass) {
        return tagTypes.get(tagClass);
    }

    static {
        for (TagType value : VALUES) {
            tagTypes.put(value.getTagClass(), value);
        }
    }
}
