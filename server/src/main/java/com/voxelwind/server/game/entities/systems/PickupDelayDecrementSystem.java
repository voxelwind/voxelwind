package com.voxelwind.server.game.entities.systems;

import com.google.common.base.VerifyException;
import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.entities.components.PickupDelay;
import com.voxelwind.api.game.entities.components.system.System;
import com.voxelwind.api.game.entities.components.system.SystemRunner;

import java.util.Optional;

public class PickupDelayDecrementSystem implements SystemRunner {
    public static final System SYSTEM = System.builder()
            .expectComponent(PickupDelay.class)
            .runner(new PickupDelayDecrementSystem())
            .build();

    private PickupDelayDecrementSystem() {

    }

    @Override
    public void run(Entity entity) {
        Optional<PickupDelay> delayOptional = entity.getComponent(PickupDelay.class);
        if (delayOptional.isPresent()) {
            delayOptional.get().setDelayPickupTicks(Math.max(0, delayOptional.get().getDelayPickupTicks() - 1));
        } else {
            throw new VerifyException("PickupDelay not found");
        }
    }
}
