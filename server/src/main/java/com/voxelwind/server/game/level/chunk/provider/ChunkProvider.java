package com.voxelwind.server.game.level.chunk.provider;

import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface ChunkProvider {
    CompletableFuture<Chunk> createChunk(Level level, int x, int z, Executor executor);
}
