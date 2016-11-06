package com.voxelwind.server.game.entities.components;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.entities.components.Health;

public class HealthComponent implements Health {
    private int health;
    private int maximumHealth;
    private boolean needsUpdate = false;

    public HealthComponent(int maximumHealth) {
        this(maximumHealth, maximumHealth);
    }

    public HealthComponent(int health, int maximumHealth) {
        Preconditions.checkArgument(health <= maximumHealth, "Health %s exceeds maximum health %s", health, maximumHealth);
        this.health = health;
        this.maximumHealth = maximumHealth;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        Preconditions.checkArgument(health <= maximumHealth, "New health %s exceeds maximum health %s", health, maximumHealth);
        this.health = health;
        this.needsUpdate = true;
    }

    @Override
    public int getMaximumHealth() {
        return maximumHealth;
    }

    @Override
    public void setMaximumHealth(int maximumHealth) {
        Preconditions.checkArgument(health > 0, "New health %s is less than minimum allowed 1", maximumHealth);
        this.maximumHealth = maximumHealth;
        this.health = Math.min(maximumHealth, health);
        this.needsUpdate = true;
    }

    public boolean needsUpdate() {
        boolean old = needsUpdate;
        needsUpdate = false;
        return old;
    }
}
