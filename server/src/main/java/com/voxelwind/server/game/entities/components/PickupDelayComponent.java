package com.voxelwind.server.game.entities.components;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.entities.components.PickupDelay;

import javax.annotation.Nonnegative;

public class PickupDelayComponent implements PickupDelay {
    private int pickupDelay = 0;

    @Override
    public int getDelayPickupTicks() {
        return pickupDelay;
    }

    @Override
    public void setDelayPickupTicks(@Nonnegative int ticks) {
        Preconditions.checkArgument(ticks >= 0, "ticks (%s) is negative", ticks);
        this.pickupDelay = ticks;
    }
}
