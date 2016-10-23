package com.voxelwind.api.game.entities;

import com.voxelwind.api.game.inventories.ArmorEquipment;

public interface Living extends Entity {
    int getHealth();

    void setHealth(int health);

    int getMaximumHealth();

    void setMaximumHealth(int maximumHealth);

    ArmorEquipment getEquipment();

    default boolean isDead() {
        return getHealth() <= 0;
    }
}
