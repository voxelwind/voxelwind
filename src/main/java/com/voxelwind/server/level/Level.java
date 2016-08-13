package com.voxelwind.server.level;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.level.manager.LevelEntityManager;
import com.voxelwind.server.level.manager.LevelPacketManager;
import com.voxelwind.server.level.provider.ChunkProvider;
import com.voxelwind.server.level.provider.LevelDataProvider;
import com.voxelwind.server.network.mcpe.packets.McpeSetTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class Level {
    private static final int FULL_TIME = 24000;
    private static final Logger LOGGER = LogManager.getLogger(Level.class);

    private final ChunkProvider chunkProvider;
    private final LevelDataProvider dataProvider;
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
        dataProvider = creator.getDataProvider();
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

    public Vector3f getSpawnLocation() {
        return dataProvider.getSpawnLocation();
    }

    public int getTime() {
        return (int) ((currentTick + dataProvider.getSavedTime()) % FULL_TIME);
    }

    public void onTick() {
        currentTick++;

        if (currentTick % 200 == 0) {
            // Broadcast a time update
            McpeSetTime time = new McpeSetTime();
            time.setRunning(true);
            time.setTime(getTime());
            packetManager.queuePacketForPlayers(time);
        }

        entityManager.onTick();
        packetManager.onTick();
    }
}
