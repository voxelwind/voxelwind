package com.voxelwind.nbt.tags;

public interface Tag<T> {
    String getName();

    T getValue();
}
