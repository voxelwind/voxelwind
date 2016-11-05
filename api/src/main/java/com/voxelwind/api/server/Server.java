package com.voxelwind.api.server;

import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.game.level.block.BlockStateBuilder;
import com.voxelwind.api.plugin.PluginManager;
import com.voxelwind.api.server.command.CommandManager;
import com.voxelwind.api.server.command.sources.ConsoleCommandExecutorSource;
import com.voxelwind.api.server.event.EventManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

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

    PluginManager getPluginManager();

    EventManager getEventManager();

    CommandManager getCommandManager();

    ConsoleCommandExecutorSource getConsoleCommandExecutorSource();

    /**
     * Returns a {@link ItemStackBuilder} for this server.
     * @return the item stack builder
     */
    ItemStackBuilder createItemStackBuilder();

    /**
     * Returns a {@link BlockStateBuilder} for this server.
     * @return the item stack builder
     */
    BlockStateBuilder createBlockStateBuilder();

    Collection<Player> getAllOnlinePlayers();

    Collection<Level> getLoadedLevels();

    CompletableFuture<Level> createLevel(LevelCreator creator);

    boolean unloadLevel(String name);
}
