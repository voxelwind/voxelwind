package com.voxelwind.server.game.entities.monsters;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.entities.monsters.Slime;
import com.voxelwind.api.game.entities.monsters.Spider;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.entities.EntityTypeData;
import com.voxelwind.server.game.entities.LivingEntity;
import com.voxelwind.server.game.entities.Spawnable;
import com.voxelwind.server.game.level.VoxelwindLevel;

@Spawnable
public class SpiderEntity extends LivingEntity implements Spider {
    public SpiderEntity(VoxelwindLevel level, Vector3f position, Server server) {
        super(EntityTypeData.SPIDER, level, position, server, 16f);
    }
}
