package com.voxelwind.server.game.entities.monsters;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.entities.monsters.MagmaCube;
import com.voxelwind.api.game.entities.monsters.Slime;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.entities.EntityTypeData;
import com.voxelwind.server.game.entities.LivingEntity;
import com.voxelwind.server.game.entities.Spawnable;
import com.voxelwind.server.game.level.VoxelwindLevel;

@Spawnable
public class SlimeEntity extends LivingEntity implements Slime {
    public SlimeEntity(VoxelwindLevel level, Vector3f position, Server server) {
        super(EntityTypeData.SLIME, level, position, server, 16);
    }
}
