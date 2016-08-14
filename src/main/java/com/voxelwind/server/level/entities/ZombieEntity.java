package com.voxelwind.server.level.entities;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.level.Level;

public class ZombieEntity extends LivingEntity {
    public ZombieEntity(Level level, Vector3f position) {
        super(32, level, position);
    }
}
