package com.voxelwind.server.level.manager;

import com.voxelwind.server.level.Level;
import com.voxelwind.server.level.entities.BaseEntity;
import com.voxelwind.server.network.session.PlayerSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This manager handles entities in levels. This class is safe for concurrent use.
 */
public class LevelEntityManager {
    private static final Logger LOGGER = LogManager.getLogger(LevelEntityManager.class);

    private final List<BaseEntity> entities = new ArrayList<>();
    private final AtomicLong entityIdAllocator = new AtomicLong();
    private final Level level;

    public LevelEntityManager(Level level) {
        this.level = level;
    }

    public synchronized void register(BaseEntity entity) {
        entities.add(entity);
    }

    public synchronized void onTick() {
        List<BaseEntity> failedToTick = new ArrayList<>();
        for (BaseEntity entity : entities) {
            try {

            } catch (Exception e) {

            }
        }

        for (BaseEntity baseEntity : failedToTick) {
            // TODO: Implement
        }
    }

    public synchronized List<PlayerSession> getPlayers() {
        List<PlayerSession> sessions = new ArrayList<>();

        for (BaseEntity entity : entities) {
            if (entity instanceof PlayerSession) {
                sessions.add((PlayerSession) entity);
            }
        }

        return sessions;
    }

    public long allocateEntityId() {
        return entityIdAllocator.incrementAndGet();
    }
}
