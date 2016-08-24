package com.voxelwind.api.server.event.player;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.util.Rotation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This event is fired after the player has been created and is about to be spawned into the world. This event should be
 * used to control where the player will be spawned at.
 */
@ParametersAreNonnullByDefault
public class PlayerSpawnEvent implements PlayerEvent {
    private final Player player;
    private Vector3f spawnLocation;
    private Level spawnLevel;
    private Rotation rotation;

    public PlayerSpawnEvent(Player player, Vector3f spawnLocation, Level spawnLevel, Rotation rotation) {
        this.player = Preconditions.checkNotNull(player, "player");
        this.spawnLocation = Preconditions.checkNotNull(spawnLocation, "spawnLocation");
        this.spawnLevel = Preconditions.checkNotNull(spawnLevel, "spawnLevel");
        this.rotation = Preconditions.checkNotNull(rotation, "rotation");
    }

    @Nonnull
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the position in the level where the player will be spawned.
     * @return a position
     */
    @Nonnull
    public Vector3f getSpawnLocation() {
        return spawnLocation;
    }

    /**
     * Sets a new position where the player will be spawned.
     * @param spawnLocation a position
     */
    public void setSpawnLocation(@Nonnull Vector3f spawnLocation) {
        this.spawnLocation = Preconditions.checkNotNull(spawnLocation, "spawnLocation");
    }

    /**
     * Returns the level where the player will be spawned.
     * @return a level
     */
    @Nonnull
    public Level getSpawnLevel() {
        return spawnLevel;
    }

    /**
     * Sets a different level for the player to be spawned in.
     * @param spawnLevel the level
     */
    public void setSpawnLevel(@Nonnull Level spawnLevel) {
        this.spawnLevel = Preconditions.checkNotNull(spawnLevel, "spawnLevel");
    }

    /**
     * Returns the rotation where the player will be spawned at.
     * @return the rotation
     */
    @Nonnull
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * Sets a new rotation fro the player to be spawned in.
     * @param rotation the rotation
     */
    public void setRotation(@Nonnull Rotation rotation) {
        this.rotation = Preconditions.checkNotNull(rotation, "rotation");
    }
}
