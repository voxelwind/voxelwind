package com.voxelwind.server.level.entities;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.level.Level;

public class LivingEntity extends BaseEntity {
    protected float drag = 0.02f;
    protected float gravity = 0.08f;

    public LivingEntity(Level level, Vector3f position) {
        super(level, position);
    }

    @Override
    public void onTick() {
        if (getMotion().lengthSquared() > 0) {
            boolean onGroundPreviously = isOnGround(getLevel(), getPosition());
            setPosition(getPosition().add(getMotion()));
            boolean onGroundNow = isOnGround(getLevel(), getPosition());

            if (!onGroundPreviously && onGroundNow) {
                setPosition(new Vector3f(getPosition().getX(), getPosition().getFloorY(), getPosition().getZ()));
                setMotion(Vector3f.ZERO);
            } else {
                setMotion(getMotion().mul(1f - drag));
                if (!onGroundNow) {
                    setMotion(getMotion().sub(0, gravity, 0));
                }
            }
        }
    }
}
