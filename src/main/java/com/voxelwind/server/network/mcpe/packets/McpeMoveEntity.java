package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.util.Rotation;
import io.netty.buffer.ByteBuf;

public class McpeMoveEntity implements RakNetPackage {
    private long entityId;
    private Vector3f position;
    private Rotation rotation;

    @Override
    public void decode(ByteBuf buffer) {
        entityId = buffer.readLong();
        position = McpeUtil.readVector3f(buffer);
        rotation = McpeUtil.readRotation(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(entityId);
        McpeUtil.writeVector3f(buffer, position);
        McpeUtil.writeRotation(buffer, rotation);
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }
}