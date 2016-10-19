package com.voxelwind.server.game.level.manager;

import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.server.game.entities.BaseEntity;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.session.PlayerSession;

import java.util.*;

public class LevelPacketManager {
    private static final int ENTITY_VIEW_DISTANCE_SQ = 64 * 64;

    private final Queue<NetworkPackage> broadcastQueue = new ArrayDeque<>();
    private final Map<Long, Queue<NetworkPackage>> specificEntityViewerQueue = new HashMap<>();
    private final VoxelwindLevel level;

    public LevelPacketManager(VoxelwindLevel level) {
        this.level = level;
    }

    public synchronized void onTick() {
        for (NetworkPackage aPackage : broadcastQueue) {
            for (PlayerSession session : level.getEntityManager().getPlayers()) {
                if (!session.isRemoved()) {
                    session.getMcpeSession().addToSendQueue(aPackage);
                }
            }
        }

        List<PlayerSession> playersInWorld = level.getEntityManager().getPlayers();
        for (Map.Entry<Long, Queue<NetworkPackage>> entry : specificEntityViewerQueue.entrySet()) {
            Optional<BaseEntity> entityById = level.getEntityManager().findEntityById(entry.getKey());
            if (entityById.isPresent()) {
                Entity entity = entityById.get();
                for (PlayerSession session : playersInWorld) {
                    if (session == entity) continue; // Don't move ourselves

                    if (session.getPosition().distanceSquared(entity.getPosition()) <= ENTITY_VIEW_DISTANCE_SQ && !session.isRemoved()) {
                        for (NetworkPackage aPackage : entry.getValue()) {
                            session.getMcpeSession().addToSendQueue(aPackage);
                        }
                    }
                }
            }
        }

        broadcastQueue.clear();
        specificEntityViewerQueue.clear();
    }

    public synchronized void queuePacketForViewers(Entity entity, NetworkPackage netPackage) {
        specificEntityViewerQueue.computeIfAbsent(entity.getEntityId(), (k) -> new ArrayDeque<>()).add(netPackage);
    }

    public synchronized void queuePacketForPlayers(NetworkPackage netPackage) {
        broadcastQueue.add(netPackage);
    }
}
