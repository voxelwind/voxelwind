package com.voxelwind.nbt.io;

public enum TagType {
    END,
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    BYTE_ARRAY,
    STRING,
    LIST,
    COMPOUND,
    INT_ARRAY;

    private static final TagType[] VALUES = values();

    public static TagType fromId(int id) {
        if (id < 0 || id >= VALUES.length) {
            return null;
        }

        return VALUES[id];
    }
}
