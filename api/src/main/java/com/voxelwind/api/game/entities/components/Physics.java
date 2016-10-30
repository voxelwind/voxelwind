package com.voxelwind.api.game.entities.components;

public interface Physics extends Component {
    float getDrag();

    void setDrag(float drag);

    double getGravity();

    void setGravity(double gravity);
}
