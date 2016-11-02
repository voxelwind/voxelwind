package com.voxelwind.server.game.entities.misc;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Verify;
import com.voxelwind.api.game.entities.components.ContainedItem;
import com.voxelwind.api.game.entities.components.Physics;
import com.voxelwind.api.game.entities.components.PickupDelay;
import com.voxelwind.api.game.entities.components.system.PhysicsSystem;
import com.voxelwind.api.game.entities.misc.DroppedItem;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.entities.BaseEntity;
import com.voxelwind.server.game.entities.EntityTypeData;
import com.voxelwind.server.game.entities.components.ContainedItemComponent;
import com.voxelwind.server.game.entities.components.PhysicsComponent;
import com.voxelwind.server.game.entities.components.PickupDelayComponent;
import com.voxelwind.server.game.entities.systems.PickupDelayDecrementSystem;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.packets.McpeAddItemEntity;

import javax.annotation.Nonnegative;
import java.util.Optional;

public class VoxelwindDroppedItem extends BaseEntity implements DroppedItem {
    public VoxelwindDroppedItem(VoxelwindLevel level, Vector3f position, Server server, ItemStack dropped) {
        super(EntityTypeData.ITEM, position, level, server);

        this.registerComponent(PickupDelay.class, new PickupDelayComponent());
        this.registerComponent(Physics.class, new PhysicsComponent());
        this.registerComponent(ContainedItem.class, new ContainedItemComponent(dropped));
        this.registerSystem(PickupDelayDecrementSystem.SYSTEM);
        this.registerSystem(PhysicsSystem.SYSTEM);
    }

    @Override
    public NetworkPackage createAddEntityPacket() {
        McpeAddItemEntity packet = new McpeAddItemEntity();
        packet.setEntityId(getEntityId());
        packet.setPosition(getGamePosition());
        packet.setVelocity(getMotion());
        packet.setStack(ensureAndGet(ContainedItem.class).getItemStack());
        return packet;
    }
}
