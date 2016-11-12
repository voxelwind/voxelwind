package com.voxelwind.server.game.level.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.spotify.futures.CompletableFutures;
import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.level.Chunk;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.game.level.chunk.SectionedChunk;
import com.voxelwind.server.game.level.chunk.generator.ChunkGenerator;
import com.voxelwind.server.game.level.chunk.provider.ChunkProvider;
import com.voxelwind.server.network.session.PlayerSession;
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
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
    private final ExecutorService loadService;

    private final VoxelwindLevel level;
    private final ChunkProvider backingChunkProvider;
    private final ChunkGenerator backingChunkGenerator;
    private final VoxelwindServer server;

    public LevelChunkManager(@Nonnull VoxelwindServer server, @Nonnull VoxelwindLevel level, @Nonnull ChunkProvider backingChunkProvider, @Nonnull ChunkGenerator backingChunkGenerator) {
        this.server = server;
        this.level = level;
        this.backingChunkProvider = backingChunkProvider;
        this.backingChunkGenerator = backingChunkGenerator;
        this.loadService = Executors.newFixedThreadPool(8, new ThreadFactoryBuilder()
                .setNameFormat("Voxelwind Chunk Load Handler for " + level.getName() + " - #%d")
                .setDaemon(true)
                .build());
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

        if (server.getConfiguration().getChunkGC().isEnabled()) {
            // Create a copy of all chunk indexes
            chunkGc: for (long chunkKey : chunksLoaded.keys()) {
                // Check for spawnchunk
                int x = (int) (chunkKey >> 32);
                int z = (int) chunkKey + Integer.MIN_VALUE;

                if (Math.abs(x - spawnChunkX) <= server.getConfiguration().getChunkGC().getSpawnRadiusToKeep() ||
                        Math.abs(z - spawnChunkZ) <= server.getConfiguration().getChunkGC().getSpawnRadiusToKeep()) {
                    // Chunk is part of the spawn, skip it
                    continue;
                }

                // Get the loaded times
                long loadedTime = loadedTimes.get(chunkKey);
                if (current - loadedTime <= TimeUnit.SECONDS.toMillis(server.getConfiguration().getChunkGC().getReleaseAfterLoadSeconds())) {
                    // This chunk has been loaded recently, skip it
                    continue;
                }

                // Check for last access
                long lastAccessTime = lastAccessTimes.get(chunkKey);
                if (current - lastAccessTime <= TimeUnit.SECONDS.toMillis(server.getConfiguration().getChunkGC().getReleaseAfterLastAccess())) {
                    // There was a access recently, skip it
                    continue;
                }

                // Check if a player has loaded the chunk
                for (PlayerSession session : level.getEntityManager().getPlayers()) {
                    if (session.isChunkInView(x, z)) {
                        // Chunk has been loaded by a player, skip it
                        continue chunkGc;
                    }
                }

                // Unload the chunk
                chunksLoaded.remove(chunkKey);
                lastAccessTimes.remove(chunkKey);
                loadedTimes.remove(chunkKey);
                level.getEntityManager().getEntitiesInChunk(x, z).forEach(Entity::remove);

                LOGGER.debug("Chunk GC cleared chunk @ " + level.getName() + " x" + x + " z" + z);
            }
        }
    }

    private class LoadingTask {
        private final int x;
        private final int z;
        private final List<CompletableFuture<Chunk>> futuresToComplete = new CopyOnWriteArrayList<>();
        private final AtomicReference<LoadState> state = new AtomicReference<>();
        private final AtomicReference<Chunk> loaded = new AtomicReference<>();
        private final AtomicReference<Throwable> loadException = new AtomicReference<>();

        private LoadingTask(int x, int z) {
            this.x = x;
            this.z = z;
        }

        boolean isInitiated() {
            return state.get() != null;
        }

        void execute() {
            // Don't run more than once!
            if (!state.compareAndSet(null, LoadState.INITIATED)) return;

            // Load the chunk.
            backingChunkProvider.createChunk(level, x, z, loadService).whenCompleteAsync((chunk, throwable) -> {
                long chunkKey = toLong(x, z);
                if (throwable != null) {
                    state.set(LoadState.EXCEPTIONAL);
                    loadException.set(throwable);
                    chunksToLoad.remove(chunkKey);
                    for (CompletableFuture<Chunk> future : futuresToComplete) {
                        future.completeExceptionally(throwable);
                    }
                    return;
                }

                if (chunk == null) {
                    long seed = chunkSeed(x, z);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Generating chunk ({},{}) using {} and seed {}", x, z, backingChunkGenerator.getClass().getName(), seed);
                    }
                    SectionedChunk generated = new SectionedChunk(x, z, level);
                    try {
                        backingChunkGenerator.generate(level, generated, new Random(seed));
                    } catch (Exception e) {
                        LOGGER.error("Exception while generating chunk ({},{})", x, z, e);

                        state.set(LoadState.EXCEPTIONAL);
                        loadException.set(e);
                        chunksToLoad.remove(chunkKey);
                        for (CompletableFuture<Chunk> future : futuresToComplete) {
                            future.completeExceptionally(e);
                        }
                        return;
                    }
                    generated.recalculateLight();
                    chunk = generated;
                }

                chunksLoaded.put(chunkKey, chunk);
                long current = System.currentTimeMillis();
                loadedTimes.put(chunkKey, current);
                lastAccessTimes.put(chunkKey, current);
                state.set(LoadState.COMPLETED);
                loaded.set(chunk);

                for (CompletableFuture<Chunk> future : futuresToComplete) {
                    future.complete(chunk);
                }
            }, loadService);
        }

        CompletableFuture<Chunk> createCompletableFuture() {
            LoadState currentState = state.get();
            if (currentState == LoadState.COMPLETED) {
                return CompletableFuture.completedFuture(loaded.get());
            } else if (currentState == LoadState.EXCEPTIONAL) {
                return CompletableFutures.exceptionallyCompletedFuture(loadException.get());
            }

            CompletableFuture<Chunk> completableFuture = new CompletableFuture<>();
            futuresToComplete.add(completableFuture);
            return completableFuture;
        }
    }

    private enum LoadState {
        INITIATED,
        COMPLETED,
        EXCEPTIONAL
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

    /**
     * Creates a chunk and level-specific random seed.
     * @param x chunk X value
     * @param z chunk Z value
     * @return random seed
     */
    private long chunkSeed(int x, int z) {
        // simple XOR of toLong() and the level seed
        return toLong(x, z) ^ level.getSeed();
    }
}
