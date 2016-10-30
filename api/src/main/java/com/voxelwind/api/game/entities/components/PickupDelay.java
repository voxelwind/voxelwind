package com.voxelwind.api.game.entities.components;

import javax.annotation.Nonnegative;

/**
 * A {@link Component} representing a pick-up delay.
 */
public interface PickupDelay extends Component {
    default boolean canPickup() {
        return getDelayPickupTicks() == 0;
    }

    @Nonnegative
    int getDelayPickupTicks();

    void setDelayPickupTicks(@Nonnegative int ticks);
}
