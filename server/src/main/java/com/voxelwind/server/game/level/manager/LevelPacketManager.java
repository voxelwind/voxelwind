package com.voxelwind.server.game.level.manager;

import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.server.game.entities.BaseEntity;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.session.PlayerSession;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import lombok.Synchronized;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LevelPacketManager {
    private static final int ENTITY_VIEW_DISTANCE_SQ = 64 * 64;

    private final Queue<NetworkPackage> broadcastQueue = new ConcurrentLinkedQueue<>();
    private final TLongObjectMap<Queue<NetworkPackage>> specificEntityViewerQueue = new TLongObjectHashMap<>();
    private final VoxelwindLevel level;

    public LevelPacketManager(VoxelwindLevel level) {
        this.level = level;
    }

    public void onTick() {
        List<PlayerSession> playersInWorld = level.getEntityManager().getPlayers();
        NetworkPackage np;
        while ((np = broadcastQueue.poll()) != null) {
            for (PlayerSession session : playersInWorld) {
                if (!session.isRemoved()) {
                    session.getMcpeSession().addToSendQueue(np);
                }
            }
        }

        synchronized (specificEntityViewerQueue) {
            specificEntityViewerQueue.forEachEntry((eid, queue) -> {
                Optional<BaseEntity> entityById = level.getEntityManager().findEntityById(eid);
                if (entityById.isPresent()) {
                    Entity entity = entityById.get();
                    for (PlayerSession session : playersInWorld) {
                        if (session == entity) continue; // Don't move ourselves

                        if (session.getPosition().distanceSquared(entity.getPosition()) <= ENTITY_VIEW_DISTANCE_SQ && !session.isRemoved()) {
                            for (NetworkPackage aPackage : queue) {
                                session.getMcpeSession().addToSendQueue(aPackage);
                            }
                        }
                    }
                }
                return true;
            });
            specificEntityViewerQueue.clear();
        }
    }

    @Synchronized("specificEntityViewerQueue")
    public void queuePacketForViewers(Entity entity, NetworkPackage netPackage) {
        Queue<NetworkPackage> packageQueue = specificEntityViewerQueue.get(entity.getEntityId());
        if (packageQueue == null) {
            specificEntityViewerQueue.put(entity.getEntityId(), packageQueue = new ArrayDeque<>());
        }
        packageQueue.add(netPackage);
    }

    public void queuePacketForPlayers(NetworkPackage netPackage) {
        broadcastQueue.add(netPackage);
    }
}
