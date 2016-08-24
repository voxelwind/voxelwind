package com.voxelwind.api.server.event.player;

import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.event.Event;

import javax.annotation.Nonnull;

/**
 * Denotes an event dealing with a player.
 */
public interface PlayerEvent extends Event {
    /**
     * Returns the relevant player in question.
     * @return the player
     */
    @Nonnull
    Player getPlayer();
}
