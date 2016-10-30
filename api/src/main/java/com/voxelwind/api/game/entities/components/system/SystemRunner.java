package com.voxelwind.api.game.entities.components.system;

import com.voxelwind.api.game.entities.Entity;

/**
 * A SystemRunner handles entity tick logic for a {@link Entity} with specified {@link com.voxelwind.api.game.entities.components.Component}s.
 */
public interface SystemRunner {
    /**
     * Ticks this system.
     * @param entity the entity being ticked
     */
    void run(Entity entity);
}
