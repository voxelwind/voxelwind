package com.voxelwind.api.server;

import com.voxelwind.api.game.level.Level;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * This class refers to the currently running Voxelwind instance.
 */
@Nonnull
public interface Server {
    /**
     * Returns the name for this server. This must be customized by forks.
     * @return the name of the currently running server software
     */
    String getName();

    /**
     * Returns the version of the server software.
     * @return the version of the server
     */
    String getVersion();

    /**
     * Retrieves a list of all currently online players.
     * @return the players online
     */
    Collection<Player> getPlayers();

    /**
     * Retrieves a list of all currently enabled and ticked worlds.
     * @return all available worlds
     */
    Collection<Level> getAllLevels();
}
