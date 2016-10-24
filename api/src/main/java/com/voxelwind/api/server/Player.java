package com.voxelwind.api.server;

import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.inventories.Inventory;
import com.voxelwind.api.game.inventories.InventoryHolder;
import com.voxelwind.api.game.inventories.PlayerInventory;
import com.voxelwind.api.server.command.CommandExecutorSource;
import com.voxelwind.api.server.event.block.iface.BlockReplacer;
import com.voxelwind.api.server.player.GameMode;
import com.voxelwind.api.server.player.PlayerMessageDisplayType;
import com.voxelwind.api.server.player.PopupMessage;
import com.voxelwind.api.server.player.TranslatedMessage;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface Player extends Entity, CommandExecutorSource, MessageRecipient, Session, InventoryHolder, BlockReplacer {
    void disconnect(@Nonnull String reason);

    Skin getSkin();

    @Nonnull
    GameMode getGameMode();

    void setGameMode(@Nonnull GameMode mode);

    void sendMessage(@Nonnull String message, @Nonnull PlayerMessageDisplayType type);

    void sendTranslatedMessage(@Nonnull TranslatedMessage message);

    void sendPopupMessage(@Nonnull PopupMessage message);

    @Override
    PlayerInventory getInventory();

    Optional<Inventory> getOpenedInventory();

    void openInventory(Inventory inventory);

    void closeInventory();

    float getBaseSpeed();

    void setBaseSpeed(float baseSpeed);
}
