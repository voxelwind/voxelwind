package com.voxelwind.api.game.entities.components;

/**
 * This {@link Component} represents an entity that is ageable.
 */
public interface Ageable {
    boolean isBaby();

    void setBaby(boolean baby);
}
