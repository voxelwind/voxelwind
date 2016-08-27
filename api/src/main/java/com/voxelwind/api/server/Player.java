package com.voxelwind.api.server;

import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.inventories.Inventory;
import com.voxelwind.api.game.inventories.InventoryHolder;
import com.voxelwind.api.game.inventories.PlayerInventory;
import com.voxelwind.api.server.command.CommandExecutorSource;
import com.voxelwind.api.server.player.GameMode;
import com.voxelwind.api.server.util.TranslatedMessage;

import javax.annotation.Nonnull;
import java.util.OptionalLong;
import java.util.UUID;

public interface Player extends Entity, CommandExecutorSource, MessageRecipient, Session, InventoryHolder {
    Skin getSkin();

    @Nonnull
    GameMode getGameMode();

    void setGameMode(@Nonnull GameMode mode);

    void sendTranslatedMessage(@Nonnull TranslatedMessage message);

    @Override
    PlayerInventory getInventory();
}
