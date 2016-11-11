package com.voxelwind.server.game.level.chunk.provider.nil;

import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.server.game.level.chunk.provider.ChunkProvider;

import java.util.concurrent.CompletableFuture;

public class NullChunkProvider implements ChunkProvider {
    @Override
    public CompletableFuture<Chunk> createChunk(Level level, int x, int z) {
        // Why are not using CompletableFuture.completedFuture() here? That's because it would interfere with badly-written
        // CompletableFuture code.
        return CompletableFuture.supplyAsync(() -> null);
    }
}
