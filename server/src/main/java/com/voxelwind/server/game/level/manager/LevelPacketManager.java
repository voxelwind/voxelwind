package com.voxelwind.server.game.level.manager;

import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.server.game.entities.BaseEntity;
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
                session.getMcpeSession().addToSendQueue(aPackage);
            }
        }

        List<PlayerSession> playersInWorld = level.getEntityManager().getPlayers();
        for (Map.Entry<Long, Queue<RakNetPackage>> entry : specificEntityViewerQueue.entrySet()) {
            Optional<BaseEntity> entityById = level.getEntityManager().findEntityById(entry.getKey());
            if (entityById.isPresent()) {
                Entity entity = entityById.get();
                for (PlayerSession session : playersInWorld) {
                    if (session == entity) continue; // Don't move ourselves

                    if (session.getPosition().distance(entity.getPosition()) <= 64F) {
                        for (RakNetPackage aPackage : entry.getValue()) {
                            session.getMcpeSession().addToSendQueue(aPackage);
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
