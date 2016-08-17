package com.voxelwind.server.game.entities;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.game.level.VoxelwindLevel;

public class ZombieEntity extends LivingEntity {
    public ZombieEntity(VoxelwindLevel level, Vector3f position) {
        super(EntityTypeData.ZOMBIE, level, position);
    }
}
