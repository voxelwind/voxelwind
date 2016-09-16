package com.voxelwind.api.game.level;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.entities.misc.DroppedItem;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.level.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a game level.
 */
@ParametersAreNonnullByDefault
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
     * Determines the chunk for a block position and returns the chunk if it is currently loaded.
     * @param position a block position
     * @return an {@link Optional}, potentially empty
     */
    default Optional<Chunk> getChunkIfLoadedForPosition(Vector3i position) {
        return getChunkIfLoaded(position.getX() >> 4, position.getY() >> 4);
    }

    /**
     * Gets a chunk, possibly loading or generating it asynchronously if required.
     * @param x the chunk's X value
     * @param z the chunk's Z value
     * @return an {@link CompletableFuture} with the chunk
     */
    CompletableFuture<Chunk> getChunk(int x, int z);

    /**
     * Determines the chunk for a block position and returns the chunk, possibly loading or generating it asynchronously if required.
     * @param position a block position
     * @return an {@link CompletableFuture} with the chunk
     */
    default CompletableFuture<Chunk> getChunkForPosition(Vector3i position) {
        return getChunk(position.getX() >> 4, position.getY() >> 4);
    }

    /**
     * Returns the block at the specified vector.
     * @param vector the vector
     * @return an {@link CompletableFuture} with the block
     */
    default CompletableFuture<Block> getBlock(@Nonnull Vector3i vector) {
        Preconditions.checkNotNull(vector, "vector");
        return getBlock(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Returns the block at the specified location, possibly loading or generating a chunk asynchronously if required.
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return an {@link CompletableFuture} with the block
     */
    default CompletableFuture<Block> getBlock(int x, int y, int z) {
        return getChunk(x >> 4, z >> 4).thenApply(chunk -> chunk.getBlock(x & 0x0f, y, z & 0x0f));
    }

    /**
     * Returns the block at the specified vector if the location it is in is already loaded.
     * @param vector the vector
     * @return an {@link Optional} with the block
     */
    default Optional<Block> getBlockIfChunkLoaded(@Nonnull Vector3i vector) {
        Preconditions.checkNotNull(vector, "vector");
        return getBlockIfChunkLoaded(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Returns the block at the specified vector.
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return an {@link Optional} with the block
     */
    default Optional<Block> getBlockIfChunkLoaded(int x, int y, int z) {
        Optional<Chunk> chunkOptional = getChunkIfLoaded(x >> 4, z >> 4);
        return chunkOptional.map(c -> c.getBlock(x & 0x0f, y, z & 0x0f));
    }

    /**
     * Spawns an entity at a specified position.
     * @param klass the entity class name from the API
     * @param position the position to spawn at
     * @param <T> entity type parameter
     * @return a new {@link Entity} instance
     */
    <T extends Entity> T spawn(Class<?> klass, Vector3f position);

    /**
     * Drops an item at a specified position.
     * @param stack the stack to drop
     * @param position the position to spawn the dropped item at
     * @return a {@link DroppedItem} instance
     */
    DroppedItem dropItem(ItemStack stack, Vector3f position);
}
