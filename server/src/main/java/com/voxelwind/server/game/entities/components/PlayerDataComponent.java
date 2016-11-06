package com.voxelwind.server.game.entities.components;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.entities.components.PlayerData;
import com.voxelwind.api.server.Skin;
import com.voxelwind.api.server.player.GameMode;
import com.voxelwind.server.network.session.PlayerSession;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public class PlayerDataComponent implements PlayerData {
    private volatile boolean gamemodeTouched = false;
    private volatile boolean attributesTouched = false;
    private volatile boolean hungerTouched = false;

    private final Skin skin;
    private volatile GameMode gameMode = GameMode.SURVIVAL;
    private volatile float speed = 0.1f;
    private volatile int hunger = 20;
    private volatile float saturation = 20f;
    private volatile float exhaustion = 4f;

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
        attributesTouched = true;
    }

    @Override
    public int getHunger() {
        return hunger;
    }

    @Override
    public void setHunger(@Nonnegative int hunger) {
        Preconditions.checkArgument(hunger >= 0 && hunger <= 20, "hunger %s not between 0 or 20", hunger);
        this.hunger = hunger;
        attributesTouched = true;
    }

    @Override
    public float getSaturation() {
        return saturation;
    }

    @Override
    public void setSaturation(@Nonnegative float saturation) {
        Preconditions.checkArgument(Float.compare(saturation, 0) > 0 && Float.compare(hunger, saturation) <= 0, "saturation must be between 0 and hunger (%s)", hunger);
        this.saturation = saturation;
        attributesTouched = true;
    }

    @Override
    public float getExhaustion() {
        return exhaustion;
    }

    @Override
    public void setExhaustion(@Nonnegative float exhaustion) {
        this.exhaustion = exhaustion;
        hungerTouched = true;
    }

    public boolean gamemodeTouched() {
        boolean prev = gamemodeTouched;
        gamemodeTouched = false;
        return prev;
    }

    public boolean attributesTouched() {
        boolean prev = attributesTouched;
        attributesTouched = false;
        return prev;
    }

    public boolean hungerTouched() {
        boolean prev = attributesTouched;
        attributesTouched = false;
        return prev;
    }
}
