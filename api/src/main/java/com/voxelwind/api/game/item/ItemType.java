package com.voxelwind.api.game.item;

import com.voxelwind.api.game.Metadata;

/**
 * This class represents a material.
 */
public interface ItemType {
    int getId();

    String getName();

    boolean isBlock();

    Class<? extends Metadata> getMetadataClass();

    int getMaximumStackSize();
}
