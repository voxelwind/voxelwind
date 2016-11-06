package com.voxelwind.server.game.entities.components;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.entities.components.PlayerData;
import com.voxelwind.api.server.Skin;
import com.voxelwind.api.server.player.GameMode;
import com.voxelwind.server.network.session.PlayerSession;

import javax.annotation.Nonnull;

public class PlayerDataComponent implements PlayerData {
    private volatile boolean gamemodeTouched = false;
    private volatile boolean speedTouched = false;

    private final Skin skin;
    private GameMode gameMode = GameMode.SURVIVAL;
    private float speed = 0.1f;

    public PlayerDataComponent(PlayerSession player) {
        this.skin = new Skin(player.getMcpeSession().getClientData().getSkinId(), player.getMcpeSession().getClientData().getSkinData());
    }

    @Override
    public Skin getSkin() {
        return skin;
    }

    @Nonnull
    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public void setGameMode(@Nonnull GameMode mode) {
        Preconditions.checkNotNull(mode, "mode");
        gameMode = mode;
        gamemodeTouched = true;
    }

    @Override
    public float getBaseSpeed() {
        return speed;
    }

    @Override
    public void setBaseSpeed(float baseSpeed) {
        Preconditions.checkArgument(Float.compare(baseSpeed, 0) > 0 && Float.compare(0.5f, baseSpeed) <= 0, "speed must be between 0 and 0.5");
        speed = baseSpeed;
        gamemodeTouched = true;
    }

    public boolean gamemodeTouched() {
        boolean prev = gamemodeTouched;
        gamemodeTouched = false;
        return prev;
    }

    public boolean speedTouched() {
        boolean prev = speedTouched;
        speedTouched = false;
        return prev;
    }
}
