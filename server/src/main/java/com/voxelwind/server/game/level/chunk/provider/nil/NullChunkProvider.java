package com.voxelwind.server.game.level.chunk.provider.nil;

import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.server.game.level.chunk.provider.ChunkProvider;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class NullChunkProvider implements ChunkProvider {
    @Override
    public CompletableFuture<Chunk> createChunk(Level level, int x, int z, Executor executor) {
        return CompletableFuture.completedFuture(null);
    }
}
