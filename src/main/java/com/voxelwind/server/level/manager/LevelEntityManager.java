package com.voxelwind.server.level.manager;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.collect.ImmutableList;
import com.voxelwind.server.level.Level;
import com.voxelwind.server.level.entities.BaseEntity;
import com.voxelwind.server.network.mcpe.packets.McpeMoveEntity;
import com.voxelwind.server.network.mcpe.packets.McpeRemoveEntity;
import com.voxelwind.server.network.mcpe.packets.McpeSetEntityMotion;
import com.voxelwind.server.network.session.PlayerSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This manager handles entities in levels. This class is safe for concurrent use.
 */
public class LevelEntityManager {
    private static final Logger LOGGER = LogManager.getLogger(LevelEntityManager.class);

    private final List<BaseEntity> entities = new ArrayList<>();
    private final Object entityLock = new Object();
    private final AtomicLong entityIdAllocator = new AtomicLong();
    private final Level level;

    public LevelEntityManager(Level level) {
        this.level = level;
    }

    public void register(BaseEntity entity) {
        synchronized (entityLock) {
            entities.add(entity);
        }
    }

    public void onTick() {
        List<BaseEntity> currentEntityList;
        synchronized (entityLock) {
            currentEntityList = ImmutableList.copyOf(entities);
        }

        List<BaseEntity> toRemove = new ArrayList<>();
        for (BaseEntity entity : currentEntityList) {
            try {
                if (!entity.onTick()) {
                    // Entity should be despawned
                    toRemove.add(entity);
                    continue;
                }

                if (entity.isStale()) {
                    // Need to send packets.
                    McpeMoveEntity moveEntityPacket = new McpeMoveEntity();
                    moveEntityPacket.setEntityId(entity.getEntityId());
                    moveEntityPacket.setPosition(entity.getGamePosition());
                    moveEntityPacket.setRotation(entity.getRotation());
                    level.getPacketManager().queuePacketForViewers(entity, moveEntityPacket);

                    McpeSetEntityMotion motionPacket = new McpeSetEntityMotion();
                    motionPacket.getMotionList().add(new McpeSetEntityMotion.EntityMotion(entity.getEntityId(), entity.getMotion()));
                    level.getPacketManager().queuePacketForViewers(entity, motionPacket);

                    entity.resetStale();
                }
            } catch (Exception e) {
                LOGGER.error("Unable to tick entity", e);
                toRemove.add(entity);
            }
        }

        for (BaseEntity entity : toRemove) {
            McpeRemoveEntity removeEntity = new McpeRemoveEntity();
            removeEntity.setEntityId(entity.getEntityId());
            level.getPacketManager().queuePacketForViewers(entity, removeEntity);
        }

        synchronized (entityLock) {
            entities.removeAll(toRemove);
        }
    }

    public List<PlayerSession> getPlayers() {
        List<BaseEntity> currentEntityList;
        synchronized (entityLock) {
            currentEntityList = ImmutableList.copyOf(entities);
        }

        List<PlayerSession> sessions = new ArrayList<>();

        for (BaseEntity entity : currentEntityList) {
            if (entity instanceof PlayerSession) {
                sessions.add((PlayerSession) entity);
            }
        }

        return sessions;
    }

    public long allocateEntityId() {
        return entityIdAllocator.incrementAndGet();
    }

    public Collection<BaseEntity> getAllEntities() {
        synchronized (entityLock) {
            return ImmutableList.copyOf(entities);
        }
    }

    public Optional<BaseEntity> findEntityById(long id) {
        synchronized (entityLock) {
            for (BaseEntity entity : entities) {
                if (entity.getEntityId() == id) {
                    return Optional.of(entity);
                }
            }
            return Optional.empty();
        }
    }

    public void unregister(BaseEntity entity) {
        synchronized (entityLock) {
            entities.remove(entity);
        }
    }

    public Collection<BaseEntity> getEntitiesInDistance(Vector3f origin, double distance) {
        Collection<BaseEntity> entities = new ArrayList<>();
        for (BaseEntity entity : getAllEntities()) {
            if (entity.getPosition().distance(origin) <= distance) {
                entities.add(entity);
            }
        }
        return entities;
    }
}
