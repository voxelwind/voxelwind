package com.voxelwind.api.server.event.player;

import com.google.common.base.Preconditions;
import com.voxelwind.api.server.Player;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This event is fired after a player has joined the game. This event should be
 * used to control what happens when a player joins.
 */
@ParametersAreNonnullByDefault
public class PlayerJoinEvent implements PlayerEvent {
    private final Player player;
    private String joinMessage;

    public PlayerJoinEvent(Player player, String joinMessage) {
        this.player = Preconditions.checkNotNull(player, "player");
        this.joinMessage = Preconditions.checkNotNull(joinMessage, "joinMessage");
    }

    @Nonnull
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the message that will be broadcast when a player joins.
     * @return the join message
     */
    @Nonnull
    public String getJoinMessage() {
        return joinMessage;
    }

    /**
     * Sets a the message that will be broadcast when a player joins.
     * @param joinMessage the join message
     */
    public void setJoinMessage(@Nonnull String joinMessage) {
        this.joinMessage = Preconditions.checkNotNull(joinMessage, "joinMessage");
    }
}
