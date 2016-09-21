package com.voxelwind.server.game.entities.monsters;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.entities.monsters.Zombie;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.entities.EntityTypeData;
import com.voxelwind.server.game.entities.LivingEntity;
import com.voxelwind.server.game.level.VoxelwindLevel;

public class ZombieEntity extends LivingEntity implements Zombie {
    public ZombieEntity(VoxelwindLevel level, Vector3f position, Server server) {
        super(EntityTypeData.ZOMBIE, level, position, server, 20f);
    }
}
