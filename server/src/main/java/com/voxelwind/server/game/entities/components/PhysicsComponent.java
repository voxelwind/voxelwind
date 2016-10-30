package com.voxelwind.server.game.entities.components;

import com.voxelwind.api.game.entities.components.Physics;

public class PhysicsComponent implements Physics {
    private float drag = 0.02f;
    private double gravity = 0.08;

    @Override
    public float getDrag() {
        return drag;
    }

    @Override
    public void setDrag(float drag) {
        this.drag = drag;
    }

    @Override
    public double getGravity() {
        return gravity;
    }

    @Override
    public void setGravity(double gravity) {
        this.gravity = gravity;
    }
}
