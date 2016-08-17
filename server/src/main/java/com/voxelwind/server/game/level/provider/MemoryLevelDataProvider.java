package com.voxelwind.server.game.level.provider;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;

public class MemoryLevelDataProvider implements LevelDataProvider {
    private int time = 0;
    private Vector3f spawn = new Vector3f(0, 5, 0);

    @Override
    public Vector3f getSpawnLocation() {
        return spawn;
    }

    @Override
    public void setSpawnLocation(Vector3f spawn) {
        this.spawn = Preconditions.checkNotNull(spawn, "spawn");
    }

    @Override
    public int getSavedTime() {
        return time;
    }

    @Override
    public void setSavedTime(int time) {
        this.time = time;
    }
}
