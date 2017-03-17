package com.voxelwind.server.game.level.manager;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.entities.components.system.System;
import com.voxelwind.server.game.entities.BaseEntity;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.game.level.util.BoundingBox;
import com.voxelwind.server.network.mcpe.packets.McpeMoveEntity;
import com.voxelwind.server.network.mcpe.packets.McpeSetEntityMotion;
import com.voxelwind.server.network.session.McpeSession;
import com.voxelwind.server.network.session.PlayerSession;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This manager handles entities in levels. This class is safe for concurrent use.
 */
public class LevelEntityManager {
    private static final Logger LOGGER = LogManager.getLogger(LevelEntityManager.class);

    private final TLongObjectMap<BaseEntity> entities = new TLongObjectHashMap<>();
    private final List<System> systems = new CopyOnWriteArrayList<>();
    private final AtomicLong entityIdAllocator = new AtomicLong();
    private final AtomicBoolean entitiesChanged = new AtomicBoolean(false);
    private final AtomicBoolean entitiesTicking = new AtomicBoolean(false);
    private final VoxelwindLevel level;

    public LevelEntityManager(VoxelwindLevel level) {
        this.level = level;
    }

    public void register(BaseEntity entity) {
        entities.put(entity.getEntityId(), entity);
        entitiesChanged.set(true);
    }

    public void registerSystem(System system) {
        systems.add(system);
    }

    public void deregisterSystem(System system) {
        systems.remove(system);
    }

    public void onTick() {
        entitiesTicking.set(true);

        TLongObjectMap<BaseEntity> copy;
        synchronized (entities) {
            copy = new TLongObjectHashMap<>(entities);
        }

        try {
            for (TLongObjectIterator<BaseEntity> it = copy.iterator(); it.hasNext(); ) {
                it.advance();
                BaseEntity entity = it.value();
                boolean isPlayer = entity instanceof PlayerSession;
                try {
                    // Check if the entity was removed.
                    if (entity.isRemoved()) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("{} was removed, discarding.", entity);
                        }
                        entitiesChanged.set(true);
                        synchronized (entities) {
                            entities.remove(entity.getEntityId());
                        }
                        continue;
                    }

                    // Tick all entity systems.
                    for (System system : systems) {
                        if (!system.isSystemCompatible(entity)) {
                            continue;
                        }
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Running entity system {} on {}", system, entity);
                        }
                        system.getRunner().run(entity);
                    }

                    // After ticking the systems, one of them may have removed the entity. Check it again.
                    if (entity.isRemoved()) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("{} was removed after systems ticked, discarding.", entity);
                        }
                        entitiesChanged.set(true);
                        synchronized (entities) {
                            entities.remove(entity.getEntityId());
                        }
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
                        motionPacket.setEntityId(entity.getEntityId());
                        motionPacket.setMotion(entity.getMotion());
                        level.getPacketManager().queuePacketForViewers(entity, motionPacket);

                        entity.resetStale();
                    }
                } catch (Exception e) {
                    entitiesChanged.set(true);
                    synchronized (entities) {
                        entities.remove(entity.getEntityId());
                    }
                    if (!isPlayer) {
                        LOGGER.error("Unable to tick entity {}. The entity will be removed.", entity, e);
                        entity.remove();
                    } else {
                        LOGGER.error("Unable to tick player {}. The player will be disconnected.", entity, e);
                        ((PlayerSession) entity).disconnect("Internal server error during tick");
                    }
                }
            }
        } finally {
            entitiesTicking.set(false);
        }

        // Perform a view check for all players if needed.
        if (entitiesChanged.compareAndSet(true, false)) {
            getPlayers().forEach(PlayerSession::updateViewableEntities);
        }
    }

    public List<PlayerSession> getPlayers() {
        synchronized (entities) {
            List<PlayerSession> sessions = new ArrayList<>();
            entities.forEachValue(entity -> {
                if (entity instanceof PlayerSession) {
                    PlayerSession session = (PlayerSession) entity;
                    McpeSession mcpeSession = session.getMcpeSession();
                    if (mcpeSession != null && !mcpeSession.isClosed() && session.isSpawned()) {
                        sessions.add((PlayerSession) entity);
                    }
                }
                return true;
            });
            return sessions;
        }
    }

    public List<Entity> getEntitiesInChunk(int x, int z) {
        synchronized (entities) {
            List<Entity> foundEntities = new ArrayList<>();
            entities.forEachValue(entity -> {
                int entityChunkX = entity.getPosition().getFloorX() >> 4;
                int entityChunkZ = entity.getPosition().getFloorZ() >> 4;

                if (!entity.isRemoved() && entityChunkX == x && entityChunkZ == z) {
                    foundEntities.add(entity);
                }
                return true;
            });
            return foundEntities;
        }
    }

    public long allocateEntityId() {
        return entityIdAllocator.incrementAndGet();
    }

    public Collection<BaseEntity> getAllEntities() {
        synchronized (entities) {
            return ImmutableList.copyOf(entities.valueCollection());
        }
    }

    public Optional<BaseEntity> findEntityById(long id) {
        synchronized (entities) {
            return Optional.ofNullable(entities.get(id));
        }
    }

    public void unregister(BaseEntity entity) {
        synchronized (entities) {
            entities.remove(entity.getEntityId());
            entitiesChanged.set(true);
        }
    }

    public Collection<BaseEntity> getEntitiesInDistance(Vector3f origin, double distance) {
        synchronized (entities) {
            Collection<BaseEntity> inDistance = new ArrayList<>();
            entities.forEachValue(entity -> {
                if (!entity.isRemoved() && entity.getPosition().distance(origin) <= distance) {
                    inDistance.add(entity);
                }
                return true;
            });
            return inDistance;
        }
    }

    public Collection<BaseEntity> getEntitiesInBounds(BoundingBox boundingBox) {
        synchronized (entities) {
            Collection<BaseEntity> inDistance = new ArrayList<>();
            entities.forEachValue(entity -> {
                if (!entity.isRemoved() && boundingBox.isWithin(entity.getPosition())) {
                    inDistance.add(entity);
                }
                return true;
            });
            return inDistance;
        }
    }

    public boolean isTicking() {
        return entitiesTicking.get();
    }
}
