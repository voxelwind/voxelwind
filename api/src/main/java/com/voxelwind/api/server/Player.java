package com.voxelwind.api.server;

import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.server.command.CommandExecutorSource;

import javax.annotation.Nonnull;
import java.util.OptionalLong;
import java.util.UUID;

public interface Player extends Entity, CommandExecutorSource, MessageRecipient, Session {
    Skin getSkin();
}
