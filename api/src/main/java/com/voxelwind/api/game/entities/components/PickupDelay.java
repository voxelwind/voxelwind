package com.voxelwind.api.game.entities.components;

import javax.annotation.Nonnegative;

/**
 * A {@link Component} that has a pick-up delay.
 */
public interface PickupDelay {
    default boolean canPickup() {
        return getDelayPickupTicks() == 0;
    }

    @Nonnegative
    int getDelayPickupTicks();

    void setDelayPickupTicks(@Nonnegative int ticks);
}
