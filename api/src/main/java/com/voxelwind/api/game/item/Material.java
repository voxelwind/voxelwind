package com.voxelwind.api.game.item;

import com.voxelwind.api.game.item.data.MaterialData;

/**
 * This class represents a material.
 */
public interface Material {
    int getId();

    boolean isBlock();

    Class<? extends MaterialData> getMaterialDataClass();
}
