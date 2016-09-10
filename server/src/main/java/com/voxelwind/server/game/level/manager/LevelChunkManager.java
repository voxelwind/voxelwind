package com.voxelwind.server.game.level.manager;

import com.flowpowered.math.vector.Vector2i;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.server.game.level.provider.ChunkProvider;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles chunk management, including loading and unloading chunks.
 *
 * This class keeps all loaded chunks in memory and cleans chunks that are no longer required to be loaded. Chunks are
 * loaded asynchronously and only one attempt to load the same chunk is made, even with concurrent callers.
 */
public class LevelChunkManager {
    private final Map<Vector2i, Chunk> chunksLoaded = new ConcurrentHashMap<>();
    private final Map<Vector2i, LoadingTask> chunksToLoad = new ConcurrentHashMap<>();
    private final Level level;
    private final ChunkProvider backingChunkProvider;

    public LevelChunkManager(@Nonnull Level level, @Nonnull ChunkProvider backingChunkProvider) {
        this.level = level;
        this.backingChunkProvider = backingChunkProvider;
    }

    public CompletableFuture<Chunk> getChunk(int x, int z) {
        Optional<Chunk> alreadyLoaded = getChunkIfLoaded(x, z);
        if (alreadyLoaded.isPresent()) {
            return CompletableFuture.completedFuture(alreadyLoaded.get());
        }

        // Not already loaded, so we need to ask the chunk provider.
        Vector2i coords = new Vector2i(x, z);
        LoadingTask loadingTask = chunksToLoad.computeIfAbsent(coords, LoadingTask::new);
        CompletableFuture<Chunk> resultFuture = loadingTask.createCompletableFuture();
        if (!loadingTask.isInitiated()) {
            loadingTask.execute();
        }
        return resultFuture;
    }

    public Optional<Chunk> getChunkIfLoaded(int x, int z) {
        Vector2i coords = new Vector2i(x, z);
        return Optional.ofNullable(chunksLoaded.get(coords));
    }

    public void onTick() {
        // TODO: Unload logic
    }

    private class LoadingTask {
        private final int x;
        private final int z;
        private final List<CompletableFuture<Chunk>> futuresToComplete = new CopyOnWriteArrayList<>();
        private final AtomicBoolean initiated = new AtomicBoolean(false);
        private final AtomicBoolean done = new AtomicBoolean(false);

        private LoadingTask(Vector2i vector2i) {
            this.x = vector2i.getX();
            this.z = vector2i.getY();
        }

        boolean isInitiated() {
            return initiated.get();
        }

        void execute() {
            // Don't run more than once!
            if (!initiated.compareAndSet(false, true)) return;

            // Load the chunk.
            backingChunkProvider.createChunk(level, x, z).whenComplete((chunk, throwable) -> {
                if (chunk != null) {
                    chunksLoaded.put(new Vector2i(x, z), chunk);
                }
                done.set(true);
                chunksToLoad.remove(new Vector2i(x, z));
                for (CompletableFuture<Chunk> future : futuresToComplete) {
                    if (throwable != null) {
                        future.completeExceptionally(throwable);
                    } else {
                        future.complete(chunk);
                    }
                }
            });
        }

        CompletableFuture<Chunk> createCompletableFuture() {
            if (done.get()) {
                Optional<Chunk> alreadyLoaded = getChunkIfLoaded(x, z);
                if (alreadyLoaded.isPresent()) {
                    return CompletableFuture.completedFuture(alreadyLoaded.get());
                }

                CompletableFuture<Chunk> exceptionalCompleted = new CompletableFuture<>();
                exceptionalCompleted.completeExceptionally(new IllegalStateException("Failed to load chunk."));
                return exceptionalCompleted;
            }
            CompletableFuture<Chunk> completableFuture = new CompletableFuture<>();
            futuresToComplete.add(completableFuture);
            return completableFuture;
        }
    }
}
