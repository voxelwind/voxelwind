package com.voxelwind.api.game.entities.components;

public interface Health extends Component {
    int getHealth();

    void setHealth(int health);

    int getMaximumHealth();

    void setMaximumHealth(int maximumHealth);

    default boolean isDead() {
        return getHealth() <= 0;
    }
}
