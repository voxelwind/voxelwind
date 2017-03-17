package com.voxelwind.server.game.entities;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.entities.components.ArmorEquipment;
import com.voxelwind.api.game.entities.components.Health;
import com.voxelwind.api.game.entities.components.Physics;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.entities.components.HealthComponent;
import com.voxelwind.server.game.entities.components.PhysicsComponent;
import com.voxelwind.server.game.inventories.VoxelwindArmorEquipment;
import com.voxelwind.server.game.level.VoxelwindLevel;

public class LivingEntity extends BaseEntity {
    protected LivingEntity(EntityTypeData data, VoxelwindLevel level, Vector3f position, Server server, int maximumHealth) {
        super(data, position, level, server);

        this.registerComponent(Health.class, new HealthComponent(maximumHealth));
        this.registerComponent(ArmorEquipment.class, new VoxelwindArmorEquipment());
        this.registerComponent(Physics.class, new PhysicsComponent());
    }
}
