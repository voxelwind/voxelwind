package com.voxelwind.api.game.entities.components;

public interface Health extends Component {
    float getHealth();

    void setHealth(float health);

    void damage(float health);

    float getMaximumHealth();

    void setMaximumHealth(float maximumHealth);

    default boolean isDead() {
        return getHealth() <= 0;
    }
}
