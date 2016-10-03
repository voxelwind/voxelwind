package com.voxelwind.server.game.level.manager;

import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.game.level.provider.ChunkProvider;
import gnu.trove.TCollections;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles chunk management, including loading and unloading chunks.
 *
 * This class keeps all loaded chunks in memory and cleans chunks that are no longer required to be loaded. Chunks are
 * loaded asynchronously and only one attempt to load the same chunk is made, even with concurrent callers.
 */
public class LevelChunkManager {
    private static final Logger LOGGER = LogManager.getLogger(LevelChunkManager.class);

    private final TLongObjectMap<Chunk> chunksLoaded = TCollections.synchronizedMap(new TLongObjectHashMap<Chunk>());
    private final TLongObjectMap<LoadingTask> chunksToLoad = TCollections.synchronizedMap(new TLongObjectHashMap<LoadingTask>());
    private final TLongLongMap loadedTimes = TCollections.synchronizedMap(new TLongLongHashMap());
    private final TLongLongMap lastAccessTimes = TCollections.synchronizedMap(new TLongLongHashMap());

    private final Level level;
    private final ChunkProvider backingChunkProvider;
    private final VoxelwindServer server;

    public LevelChunkManager(@Nonnull VoxelwindServer server, @Nonnull Level level, @Nonnull ChunkProvider backingChunkProvider) {
        this.server = server;
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
        long chunkKey = toLong(x, z);
        Chunk chunk = chunksLoaded.get(chunkKey);
        if (chunk != null) {
            lastAccessTimes.put(chunkKey, System.currentTimeMillis());
        }

        return Optional.ofNullable(chunk);
    }

    public void onTick() {
        long current = System.currentTimeMillis();

        int spawnChunkX = level.getSpawnLocation().getFloorX() >> 4;
        int spawnChunkZ = level.getSpawnLocation().getFloorZ() >> 4;

        // Create a copy of all chunk indexes
        for (long chunkKey : chunksLoaded.keys()) {
            // Check for spawnchunk
            int x = (int) (chunkKey >> 32);
            int z = (int) chunkKey + Integer.MIN_VALUE;

            if ( Math.abs( x - spawnChunkX ) <= server.getConfiguration().getChunkGC().getSpawnRadiusToKeep() ||
                    Math.abs( z - spawnChunkZ ) <= server.getConfiguration().getChunkGC().getSpawnRadiusToKeep() ) {
                // Chunk is part of the spawn, skip it
                continue;
            }

            // Get the loaded times
            long loadedTime = loadedTimes.get(chunkKey);
            if (current - loadedTime < TimeUnit.SECONDS.toMillis(server.getConfiguration().getChunkGC().getReleaseAfterLoadSeconds())) {
                // This chunk has been loaded recently, skip it
                continue;
            }

            // Check for last access
            long lastAccessTime = lastAccessTimes.get(chunkKey);
            if ( current - lastAccessTime < TimeUnit.SECONDS.toMillis(server.getConfiguration().getChunkGC().getReleaseAfterLastAccess())) {
                // There was a access recently, skip it
                continue;
            }

            // Unload the chunk
            chunksLoaded.remove(chunkKey);
            lastAccessTimes.remove(chunkKey);
            loadedTimes.remove(chunkKey);

            LOGGER.debug("Chunk GC cleared chunk @ " + level.getName() + " x" + x + " z" + z );
        }
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
                long current = System.currentTimeMillis();
                loadedTimes.put(chunkKey, current);
                lastAccessTimes.put(chunkKey, current);

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
