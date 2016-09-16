package com.voxelwind.server.game.entities.monsters;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.entities.monsters.Zombie;
import com.voxelwind.server.game.entities.EntityTypeData;
import com.voxelwind.server.game.entities.LivingEntity;
import com.voxelwind.server.game.level.VoxelwindLevel;

public class ZombieEntity extends LivingEntity implements Zombie {
    public ZombieEntity(VoxelwindLevel level, Vector3f position) {
        super(EntityTypeData.ZOMBIE, level, position, 20f);
    }
}
