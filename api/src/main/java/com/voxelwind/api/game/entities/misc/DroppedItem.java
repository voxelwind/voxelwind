package com.voxelwind.api.game.entities.misc;

import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.item.ItemStack;

public interface DroppedItem extends Entity {
    ItemStack getItemStack();
}
