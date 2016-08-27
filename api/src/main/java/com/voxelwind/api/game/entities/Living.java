package com.voxelwind.api.game.entities;

import com.voxelwind.api.game.inventories.ArmorEquipment;

public interface Living extends Entity {
    float getHealth();

    void setHealth(float health);

    float getMaximumHealth();

    void setMaximumHealth(float maximumHealth);

    ArmorEquipment getEquipment();
}
