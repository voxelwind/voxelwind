package com.voxelwind.server.level.entities;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.level.Level;
import com.voxelwind.server.network.session.PlayerSession;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEntity {
    private long entityId;
    private Vector3f position;
    private Vector3f motion;
    private Level level;

    public BaseEntity(long entityId, Level level, Vector3f position) {
        this.entityId = entityId;
        this.level = level;
        this.position = position;
        this.motion = Vector3f.ZERO;
    }

    protected void onTick() {
        motion = motion.mul(0.95, 0.97, 0.95);
        position = position.add(motion);

        for (PlayerSession session : level.getPlayers()) {

        }
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getMotion() {
        return motion;
    }

    public void setMotion(Vector3f motion, boolean broadcast) {
        this.motion = motion;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<PlayerSession> getPlayersWithinDistance() {
        List<PlayerSession> sessions = new ArrayList<>();
        for (PlayerSession session : level.getPlayers()) {
            if (position.distance(session.getPosition()) <= 64f) {
                sessions.add(session);
            }
        }
        return sessions;
    }
}
