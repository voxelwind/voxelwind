package com.voxelwind.api.game.item;

import com.voxelwind.api.game.item.data.ItemData;

import java.util.Optional;

/**
 * This class represents a material.
 */
public interface ItemType {
    int getId();

    String getName();

    boolean isBlock();

    Class<? extends ItemData> getMaterialDataClass();

    int getMaximumStackSize();

    Optional<ItemData> createDataFor(short metadata);
}
