package com.voxelwind.api.server;

import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.server.command.CommandExecutorSource;
import com.voxelwind.api.server.player.GameMode;

import javax.annotation.Nonnull;
import java.util.OptionalLong;
import java.util.UUID;

public interface Player extends Entity, CommandExecutorSource, MessageRecipient, Session {
    Skin getSkin();

    @Nonnull
    GameMode getGameMode();

    void setGameMode(@Nonnull GameMode mode);
}
