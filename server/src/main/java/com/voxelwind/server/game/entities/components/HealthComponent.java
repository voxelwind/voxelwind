package com.voxelwind.server.game.entities.components;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.entities.components.Health;

public class HealthComponent implements Health {
    private float health;
    private float maximumHealth;
    private boolean needsUpdate = false;

    public HealthComponent(float maximumHealth) {
        this(maximumHealth, maximumHealth);
    }

    public HealthComponent(float health, float maximumHealth) {
        Preconditions.checkArgument(health <= maximumHealth, "Health %s exceeds maximum health %s", health, maximumHealth);
        this.health = health;
        this.maximumHealth = maximumHealth;
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    public void setHealth(float health) {
        Preconditions.checkArgument(health <= maximumHealth, "New health %s exceeds maximum health %s", health, maximumHealth);
        this.health = health;
        this.needsUpdate = true;
    }

    @Override
    public void damage(float health) {
        setHealth(this.health - health);
    }

    @Override
    public float getMaximumHealth() {
        return maximumHealth;
    }

    @Override
    public void setMaximumHealth(float maximumHealth) {
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
