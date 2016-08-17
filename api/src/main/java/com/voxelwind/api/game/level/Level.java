package com.voxelwind.api.game.level;

import com.flowpowered.math.vector.Vector3f;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Level {
    String getName();

    UUID getUuid();

    long getCurrentTick();

    Vector3f getSpawnLocation();

    int getTime();

    Optional<Chunk> getChunkIfLoaded(int x, int z);

    CompletableFuture<Chunk> getChunk(int x, int z);
}
