package com.voxelwind.server.level.manager;

import com.voxelwind.server.level.VoxelwindLevel;
import com.voxelwind.server.level.entities.Entity;
import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.session.PlayerSession;

import java.util.*;

public class LevelPacketManager {
    private final Queue<RakNetPackage> broadcastQueue = new ArrayDeque<>();
    private final Map<Long, Queue<RakNetPackage>> specificEntityViewerQueue = new HashMap<>();
    private final VoxelwindLevel level;

    public LevelPacketManager(VoxelwindLevel level) {
        this.level = level;
    }

    public synchronized void onTick() {
        for (RakNetPackage aPackage : broadcastQueue) {
            for (PlayerSession session : level.getEntityManager().getPlayers()) {
                session.getUserSession().addToSendQueue(aPackage);
            }
        }

        List<PlayerSession> playersInWorld = level.getEntityManager().getPlayers();
        for (Map.Entry<Long, Queue<RakNetPackage>> entry : specificEntityViewerQueue.entrySet()) {
            Optional<Entity> entityById = level.getEntityManager().findEntityById(entry.getKey());
            if (entityById.isPresent()) {
                Entity entity = entityById.get();
                for (PlayerSession session : playersInWorld) {
                    if (session == entity) continue; // Don't move ourselves

                    if (session.getPosition().distance(entity.getPosition()) <= 64F) {
                        for (RakNetPackage aPackage : entry.getValue()) {
                            session.getUserSession().addToSendQueue(aPackage);
                        }
                    }
                }
            }
        }

        broadcastQueue.clear();
        specificEntityViewerQueue.clear();
    }

    public synchronized void queuePacketForViewers(Entity entity, RakNetPackage netPackage) {
        specificEntityViewerQueue.computeIfAbsent(entity.getEntityId(), (k) -> new ArrayDeque<>()).add(netPackage);
    }

    public synchronized void queuePacketForPlayers(RakNetPackage netPackage) {
        broadcastQueue.add(netPackage);
    }
}
