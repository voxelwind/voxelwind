package com.voxelwind.server.level.provider;

import com.voxelwind.server.level.chunk.Chunk;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ChunkProvider {
    CompletableFuture<Chunk> get(int x, int z);

    Optional<Chunk> getIfLoaded(int x, int z);

    boolean unload(int x, int z);
}
