package com.voxelwind.api.game.entities.components;

import com.voxelwind.api.game.entities.Entity;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * This {@link Component} represents an entity that acts as a vehicle.
 */
public interface Vehicle {
    Optional<Entity> getPassenger();

    void ejectPassenger();

    void setPassenger(@Nonnull Entity passenger);
}
