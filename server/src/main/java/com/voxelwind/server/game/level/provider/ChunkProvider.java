package com.voxelwind.server.game.level.provider;

import com.voxelwind.api.game.level.Chunk;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ChunkProvider {
    CompletableFuture<Chunk> get(int x, int z);

    Optional<Chunk> getIfLoaded(int x, int z);

    boolean unload(int x, int z);
}
