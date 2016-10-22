package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;

public class McpeRemoveBlock implements NetworkPackage {
    private long entityId;
    private Vector3i position;

    @Override
    public void decode(ByteBuf buffer) {
        entityId = buffer.readLong();
        position = McpeUtil.readBlockCoords(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(entityId);
        McpeUtil.writeBlockCoords(buffer, position);
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public Vector3i getPosition() {
        return position;
    }

    public void setPosition(Vector3i position) {
        this.position = position;
    }
}
