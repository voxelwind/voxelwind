package com.voxelwind.server.game.level.manager;

import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.server.game.level.provider.ChunkProvider;
import gnu.trove.TCollections;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles chunk management, including loading and unloading chunks.
 *
 * This class keeps all loaded chunks in memory and cleans chunks that are no longer required to be loaded. Chunks are
 * loaded asynchronously and only one attempt to load the same chunk is made, even with concurrent callers.
 */
public class LevelChunkManager {
    private final TLongObjectMap<Chunk> chunksLoaded = TCollections.synchronizedMap(new TLongObjectHashMap<Chunk>());
    private final TLongObjectMap<LoadingTask> chunksToLoad = TCollections.synchronizedMap(new TLongObjectHashMap<LoadingTask>());
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
        long chunkKey = toLong(x, z);
        LoadingTask loadingTask = chunksToLoad.get(chunkKey);
        if (loadingTask == null) {
            loadingTask = new LoadingTask(x, z);
            chunksToLoad.put(chunkKey, loadingTask);
        }

        CompletableFuture<Chunk> resultFuture = loadingTask.createCompletableFuture();
        if (!loadingTask.isInitiated()) {
            loadingTask.execute();
        }

        return resultFuture;
    }

    public Optional<Chunk> getChunkIfLoaded(int x, int z) {
        return Optional.ofNullable(chunksLoaded.get(toLong(x, z)));
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

        private LoadingTask(int x, int z) {
            this.x = x;
            this.z = z;
        }

        boolean isInitiated() {
            return initiated.get();
        }

        void execute() {
            // Don't run more than once!
            if (!initiated.compareAndSet(false, true)) return;

            // Load the chunk.
            backingChunkProvider.createChunk(level, x, z).whenComplete((chunk, throwable) -> {
                long chunkKey = toLong(x, z);
                if (chunk != null) {
                    chunksLoaded.put(chunkKey, chunk);
                }

                done.set(true);
                chunksToLoad.remove(chunkKey);
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

    /**
     * Shift two int's together to form a compound key
     *
     * @param x value of key
     * @param z value of key
     * @return long compound of the two int's
     */
    private static long toLong(int x, int z) {
        return ((long) x << 32) + z - Integer.MIN_VALUE;
    }
}
