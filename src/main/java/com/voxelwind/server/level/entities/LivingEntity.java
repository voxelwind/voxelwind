package com.voxelwind.server.level.entities;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import com.voxelwind.server.level.Level;
import com.voxelwind.server.network.mcpe.packets.McpeEntityEvent;

public class LivingEntity extends BaseEntity {
    protected float drag = 0.02f;
    protected float gravity = 0.08f;
    private float health;
    private float maximumHealth;

    public LivingEntity(EntityTypeData data, Level level, Vector3f position) {
        super(data, position, level);
        // TODO: Set health.
    }

    @Override
    public boolean onTick() {
        super.onTick();

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

        return true;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        checkIfAlive();

        Preconditions.checkArgument(Float.compare(health, maximumHealth) <= 0, "New health %s exceeds maximum health %s", health, maximumHealth);
        this.health = health;

        if (Double.compare(health, maximumHealth) <= 0f) {
            doDeath();
        }
    }

    public float getMaximumHealth() {
        return maximumHealth;
    }

    public void setMaximumHealth(float maximumHealth) {
        this.maximumHealth = maximumHealth;
        this.health = Math.min(maximumHealth, health);
    }

    protected void doDeath() {
        McpeEntityEvent event = new McpeEntityEvent();
        event.setEntityId(getEntityId());
        event.setEvent((byte) 3);
        getLevel().getPacketManager().queuePacketForViewers(this, event);

        // Technically, the entity will live for one extra tick, but that shouldn't matter.
        remove();
    }
}
