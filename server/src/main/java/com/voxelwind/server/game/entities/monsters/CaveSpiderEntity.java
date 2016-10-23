package com.voxelwind.server.game.entities.monsters;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.entities.monsters.CaveSpider;
import com.voxelwind.api.game.entities.monsters.Zombie;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.entities.EntityTypeData;
import com.voxelwind.server.game.entities.LivingEntity;
import com.voxelwind.server.game.entities.Spawnable;
import com.voxelwind.server.game.level.VoxelwindLevel;

@Spawnable
public class CaveSpiderEntity extends LivingEntity implements CaveSpider {
    public CaveSpiderEntity(VoxelwindLevel level, Vector3f position, Server server) {
        super(EntityTypeData.CAVE_SPIDER, level, position, server, 12);
    }
}
