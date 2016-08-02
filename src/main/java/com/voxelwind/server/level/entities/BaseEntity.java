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

    public void setStale(boolean stale) {
        this.stale = stale;
    }

    public void onTick() {
        // Does nothing
    }

    protected static boolean isOnGround(Level level, Vector3f position) {
        Vector3i blockPosition = position.sub(0f, 0.1f, 0f).toInt();
        // TODO: Implement.
        return true;
    }
}
