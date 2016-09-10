package com.voxelwind.server.game.level;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.server.game.level.manager.LevelChunkManager;
import com.voxelwind.server.game.level.manager.LevelEntityManager;
import com.voxelwind.server.game.level.manager.LevelPacketManager;
import com.voxelwind.server.game.level.provider.ChunkProvider;
import com.voxelwind.server.game.level.provider.LevelDataProvider;
import com.voxelwind.server.network.mcpe.packets.McpeSetTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VoxelwindLevel implements Level {
    private static final int FULL_TIME = 24000;
    private static final Logger LOGGER = LogManager.getLogger(VoxelwindLevel.class);

    private final LevelChunkManager chunkManager;
    private final LevelDataProvider dataProvider;
    private final String name;
    private final UUID uuid;
    private final LevelEntityManager entityManager;
    private final LevelPacketManager packetManager;
    private long currentTick;

    public VoxelwindLevel(LevelCreator creator) {
        chunkManager = new LevelChunkManager(this, creator.getChunkProvider());
        name = creator.getName();
        uuid = UUID.randomUUID();
        entityManager = new LevelEntityManager(this);
        packetManager = new LevelPacketManager(this);
        dataProvider = creator.getDataProvider();
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    public LevelEntityManager getEntityManager() {
        return entityManager;
    }

    public LevelPacketManager getPacketManager() {
        return packetManager;
    }

    @Override
    public long getCurrentTick() {
        return currentTick;
    }

    @Override
    public Vector3f getSpawnLocation() {
        return dataProvider.getSpawnLocation();
    }

    @Override
    public int getTime() {
        return (int) ((currentTick + dataProvider.getSavedTime()) % FULL_TIME);
    }

    @Override
    public Optional<Chunk> getChunkIfLoaded(int x, int z) {
        return chunkManager.getChunkIfLoaded(x, z);
    }

    @Override
    public CompletableFuture<Chunk> getChunk(int x, int z) {
        return chunkManager.getChunk(x, z);
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
