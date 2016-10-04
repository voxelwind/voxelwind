package com.voxelwind.server.game.entities.monsters;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.entities.monsters.MagmaCube;
import com.voxelwind.api.game.entities.monsters.Silverfish;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.entities.EntityTypeData;
import com.voxelwind.server.game.entities.LivingEntity;
import com.voxelwind.server.game.entities.Spawnable;
import com.voxelwind.server.game.level.VoxelwindLevel;

@Spawnable
public class SilvefishEntity extends LivingEntity implements Silverfish {
    public SilvefishEntity(VoxelwindLevel level, Vector3f position, Server server) {
        super(EntityTypeData.SILVERFISH, level, position, server, 8f);
    }
}
