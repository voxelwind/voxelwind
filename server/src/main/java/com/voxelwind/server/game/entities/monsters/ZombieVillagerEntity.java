package com.voxelwind.server.game.entities.monsters;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.entities.monsters.ZombiePigman;
import com.voxelwind.api.game.entities.monsters.ZombieVillager;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.entities.EntityTypeData;
import com.voxelwind.server.game.entities.LivingEntity;
import com.voxelwind.server.game.entities.Spawnable;
import com.voxelwind.server.game.level.VoxelwindLevel;

@Spawnable
public class ZombieVillagerEntity extends LivingEntity implements ZombieVillager {
    public ZombieVillagerEntity(VoxelwindLevel level, Vector3f position, Server server) {
        super(EntityTypeData.ZOMBIE_VILLAGER, level, position, server, 20f);
    }
}
