package com.voxelwind.server.level;

import com.flowpowered.math.vector.Vector3f;

import java.util.UUID;

public interface Level {
    String getName();

    UUID getUuid();

    long getCurrentTick();

    Vector3f getSpawnLocation();

    int getTime();
}
