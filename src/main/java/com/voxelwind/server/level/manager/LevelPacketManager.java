package com.voxelwind.server.level.manager;

import com.voxelwind.server.level.Level;
import com.voxelwind.server.network.raknet.RakNetPackage;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class LevelPacketManager {
    private final Queue<RakNetPackage> broadcastQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentMap<Long, Queue<RakNetPackage>> specificEntityViewerQueue = new ConcurrentHashMap<>();
    private final Level level;

    public LevelPacketManager(Level level) {
        this.level = level;
    }

    public void onTick() {

    }
}
