package com.voxelwind.server.game.entities.misc;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.entities.misc.DroppedItem;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.entities.BaseEntity;
import com.voxelwind.server.game.entities.EntityTypeData;
import com.voxelwind.server.game.entities.Spawnable;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.packets.McpeAddItem;

public class VoxelwindDroppedItem extends BaseEntity implements DroppedItem {
    private final ItemStack dropped;

    public VoxelwindDroppedItem(VoxelwindLevel level, Vector3f position, Server server, ItemStack dropped) {
        super(EntityTypeData.ITEM, position, level, server);
        this.dropped = dropped;
    }

    @Override
    public ItemStack getItemStack() {
        return dropped;
    }

    @Override
    public NetworkPackage createAddEntityPacket() {
        McpeAddItem packet = new McpeAddItem();
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
        return true;
    }
}
