package com.voxelwind.server.level.entities;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.server.level.Level;
import com.voxelwind.server.util.Rotation;

public class BaseEntity {
    private long entityId;
    private Level level;
    private Vector3f position;
    private Vector3f motion;
    private Rotation rotation;
    private boolean stale = true;
    private boolean teleported = false;

    public BaseEntity(Level level, Vector3f position) {
        this.level = Preconditions.checkNotNull(level, "level");
        this.position = Preconditions.checkNotNull(position, position);
        this.entityId = level.getEntityManager().allocateEntityId();
        this.rotation = Rotation.ZERO;
        this.motion = Vector3f.ZERO;
    }

    public long getEntityId() {
        return entityId;
    }

    public Level getLevel() {
        return level;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Vector3f getMotion() {
        return motion;
    }

    protected void setPosition(Vector3f position) {
        if (!this.position.equals(position)) {
            this.position = position;
            stale = true;
        }
    }

    public void setMotion(Vector3f motion) {
        if (!this.motion.equals(motion)) {
            this.motion = motion;
            stale = true;
        }
    }

    public void setRotation(Rotation rotation) {
        if (!this.rotation.equals(rotation)) {
            this.rotation = rotation;
            stale = true;
        }
    }

    public boolean isStale() {
        return stale;
    }

    public void onTick() {
        // Does nothing
    }

    public boolean isOnGround() {
        return isOnGround(level, position);
    }

    public boolean isTeleported() {
        return teleported;
    }

    protected static boolean isOnGround(Level level, Vector3f position) {
        Vector3i blockPosition = position.sub(0f, 0.1f, 0f).toInt();
        // TODO: Implement.
        return true;
    }

    public void resetStale() {
        stale = false;
        teleported = false;
    }

    public void teleport(Vector3f position) {
        teleport(level, position, rotation);
    }

    public void teleport(Level level, Vector3f position) {
        teleport(level, position, rotation);
    }

    public void teleport(Level level, Vector3f position, Rotation rotation) {
        Level oldLevel = this.level;
        if (oldLevel != level) {
            throw new UnsupportedOperationException();
            // TODO: Fix?
            //oldLevel.getEntityManager().unregister(this);
        }
        this.level = level;
        setPosition(position);
        setRotation(rotation);
        this.teleported = true;
    }
}
