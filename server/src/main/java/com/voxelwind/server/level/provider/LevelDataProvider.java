package com.voxelwind.server.level.provider;

import com.flowpowered.math.vector.Vector3f;

public interface LevelDataProvider {
    Vector3f getSpawnLocation();
    void setSpawnLocation(Vector3f spawn);
    int getSavedTime();
    void setSavedTime(int time);
}
