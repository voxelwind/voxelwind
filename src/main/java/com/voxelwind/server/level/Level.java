package com.voxelwind.server.level;

import com.voxelwind.server.level.manager.LevelEntityManager;
import com.voxelwind.server.level.manager.LevelPacketManager;
import com.voxelwind.server.level.provider.ChunkProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class Level {
    private static final Logger LOGGER = LogManager.getLogger(Level.class);

    private final ChunkProvider chunkProvider;
    private final String name;
    private final UUID uuid;
    private final LevelEntityManager entityManager;
    private final LevelPacketManager packetManager;
    private long currentTick;

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

    public long getCurrentTick() {
        return currentTick;
    }

    public void onTick() {
        currentTick++;
        entityManager.onTick();
        packetManager.onTick();
    }
}
