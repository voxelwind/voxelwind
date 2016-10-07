package com.voxelwind.api.game.entities.misc;

import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.item.ItemStack;

import javax.annotation.Nonnegative;

public interface DroppedItem extends Entity {
    ItemStack getItemStack();

    boolean canPickup();

    int getDelayPickupTicks();

    void setDelayPickupTicks(@Nonnegative int ticks);
}
