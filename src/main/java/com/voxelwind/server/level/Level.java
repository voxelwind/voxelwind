package com.voxelwind.server.level;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.collect.ImmutableList;
import com.voxelwind.server.level.chunk.Chunk;
import com.voxelwind.server.level.provider.ChunkProvider;
import com.voxelwind.server.network.session.PlayerSession;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Level {
    private final ChunkProvider chunkProvider;
    private final UUID uuid;
    private final List<PlayerSession> players = new ArrayList<>();

    public Level(ChunkProvider chunkProvider, UUID uuid) {
        this.chunkProvider = chunkProvider;
        this.uuid = uuid;
    }

    public Vector3f getSpawnLocation() {
        return chunkProvider.getSpawn();
    }

    public CompletableFuture<Chunk> getChunk(int x, int z) {
        return chunkProvider.get(x, z);
    }

    synchronized void onTick() {

    }

    public synchronized void addPlayer(PlayerSession playerSession) {
        players.add(playerSession);
    }

    public synchronized List<PlayerSession> getPlayers() {
        return ImmutableList.copyOf(players);
    }

    public synchronized void removePlayer(PlayerSession playerSession) {
        players.remove(playerSession);
    }
}
