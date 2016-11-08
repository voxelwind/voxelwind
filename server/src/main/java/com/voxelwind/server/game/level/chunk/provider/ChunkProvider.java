package com.voxelwind.server.game.level.chunk.provider;

import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;

import java.util.concurrent.CompletableFuture;

public interface ChunkProvider {
    CompletableFuture<Chunk> createChunk(Level level, int x, int z);
}
