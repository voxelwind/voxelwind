package com.voxelwind.server.game.entities.misc;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.entities.misc.DroppedItem;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.entities.BaseEntity;
import com.voxelwind.server.game.entities.EntityTypeData;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.packets.McpeAddItemEntity;

import javax.annotation.Nonnegative;

public class VoxelwindDroppedItem extends BaseEntity implements DroppedItem {
    private final ItemStack dropped;
    private int currentDelayPickupTicks;

    public VoxelwindDroppedItem(VoxelwindLevel level, Vector3f position, Server server, ItemStack dropped) {
        super(EntityTypeData.ITEM, position, level, server);
        this.dropped = dropped;
    }

    @Override
    public ItemStack getItemStack() {
        return dropped;
    }

    @Override
    public boolean canPickup() {
        return currentDelayPickupTicks == 0;
    }

    @Override
    public int getDelayPickupTicks() {
        return currentDelayPickupTicks;
    }

    @Override
    public void setDelayPickupTicks(@Nonnegative int ticks) {
        currentDelayPickupTicks = ticks;
    }

    @Override
    public NetworkPackage createAddEntityPacket() {
        McpeAddItemEntity packet = new McpeAddItemEntity();
        packet.setEntityId(getEntityId());
        packet.setPosition(getGamePosition());
        packet.setVelocity(getMotion());
        packet.setStack(dropped);
        return packet;
    }

    @Override
    public boolean onTick() {
        if (!super.onTick()) {
            return false;
        }

        doMovement();
        if (currentDelayPickupTicks > 0) {
            currentDelayPickupTicks--;
        }
        return true;
    }
}
