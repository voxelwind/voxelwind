package com.voxelwind.server.game.entities.systems;

import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.entities.components.Health;
import com.voxelwind.api.game.entities.components.system.System;
import com.voxelwind.api.game.entities.components.system.SystemRunner;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.network.mcpe.packets.McpeEntityEvent;

import java.util.function.Consumer;

public class DeathSystem implements SystemRunner {
    public static final System GENERIC = System.builder()
            .expectComponent(Health.class)
            .runner(new DeathSystem(entity -> {
                McpeEntityEvent event = new McpeEntityEvent();
                event.setEntityId(entity.getEntityId());
                event.setEvent((byte) 3);
                ((VoxelwindLevel) entity.getLevel()).getPacketManager().queuePacketForViewers(entity, event);

                // Technically, the entity will live for one extra tick, but that shouldn't matter.
                if (entity instanceof CustomDeath) {
                    ((CustomDeath) entity).doDeath();
                } else {
                    entity.remove();
                }
            }))
            .build();

    private final Consumer<Entity> onDeath;

    public DeathSystem(Consumer<Entity> onDeath) {
        this.onDeath = onDeath;
    }

    @Override
    public void run(Entity entity) {
        Health health = entity.ensureAndGet(Health.class);
        if (health.isDead()) {
            onDeath.accept(entity);
        }
    }

    public interface CustomDeath {
        void doDeath();
    }
}
