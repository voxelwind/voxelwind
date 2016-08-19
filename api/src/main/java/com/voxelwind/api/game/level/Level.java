package com.voxelwind.api.game.level;

import com.flowpowered.math.vector.Vector3f;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a game level.
 */
public interface Level {
    /**
     * Returns the name of the world.
     * @return the world's name
     */
    String getName();

    /**
     * Returns the unique ID of this world.
     * @return the world's UUID
     */
    UUID getUuid();

    /**
     * Returns the current tick.
     * @return the current tick
     */
    long getCurrentTick();

    /**
     * Returns the world's spawn location.
     * @return the spawn location
     */
    Vector3f getSpawnLocation();

    /**
     * Retrieves the current time of the world.
     * @return the world's time
     */
    int getTime();

    /**
     * Gets a chunk if it is currently loaded.
     * @param x the chunk's X value
     * @param z the chunk's Z value
     * @return an {@link Optional}, potentially empty
     */
    Optional<Chunk> getChunkIfLoaded(int x, int z);

    /**
     * Gets a chunk, possibly loading or generating it asynchronously if required.
     * @param x the chunk's X value
     * @param z the chunk's Z value
     * @return an {@link CompletableFuture} with the chunk
     */
    CompletableFuture<Chunk> getChunk(int x, int z);
}
