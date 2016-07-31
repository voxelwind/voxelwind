package com.voxelwind.server.level.entities;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import com.voxelwind.server.level.Level;
import com.voxelwind.server.util.Rotation;

public class BaseEntity {
    private long entityId;
    private Level level;
    private Vector3f position;
    private Rotation rotation;

    public BaseEntity(Level level, Vector3f position) {
        this.level = Preconditions.checkNotNull(level, "level");
        this.position = Preconditions.checkNotNull(position, position);
        this.entityId = level.getEntityManager().allocateEntityId();
        this.rotation = Rotation.ZERO;
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
}
