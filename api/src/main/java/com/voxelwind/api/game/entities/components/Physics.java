package com.voxelwind.api.game.entities.components;

/**
 * A {@link Component} that allows you basic control over an {@link com.voxelwind.api.game.entities.Entity}'s physics.
 * If you want more sophisticated physics, you should implement your own {@link System}.
 */
public interface Physics extends Component {
    float getDrag();

    void setDrag(float drag);

    double getGravity();

    void setGravity(double gravity);
}
