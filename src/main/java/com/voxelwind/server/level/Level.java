package com.voxelwind.server.level;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.collect.ImmutableList;
import com.voxelwind.server.level.chunk.Chunk;
import com.voxelwind.server.level.entities.BaseEntity;
import com.voxelwind.server.level.manager.LevelEntityManager;
import com.voxelwind.server.level.manager.LevelPacketManager;
import com.voxelwind.server.level.provider.ChunkProvider;
import com.voxelwind.server.network.session.PlayerSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public class Level {
    private static final Logger LOGGER = LogManager.getLogger(Level.class);

    private final ChunkProvider chunkProvider;
    private final String name;
    private final UUID uuid;
    private final LevelEntityManager entityManager;
    private final LevelPacketManager packetManager;

    public Level(LevelCreator creator) {
        chunkProvider = creator.getChunkProvider();
        name = creator.getName();
        uuid = UUID.randomUUID(); // TODO: Fix?
        entityManager = new LevelEntityManager(this);
        packetManager = new LevelPacketManager(this);
    }

    public ChunkProvider getChunkProvider() {
        return chunkProvider;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public LevelEntityManager getEntityManager() {
        return entityManager;
    }

    public LevelPacketManager getPacketManager() {
        return packetManager;
    }
}
