package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.level.util.Attribute;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collection;

public class McpeUpdateAttributes implements RakNetPackage {
    private long entityId;
    private final Collection<Attribute> attributes = new ArrayList<>();

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public Collection<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public void decode(ByteBuf buffer) {
        entityId = buffer.readLong();
        attributes.addAll(McpeUtil.readAttributes(buffer));
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(entityId);
        McpeUtil.writeAttributes(buffer, attributes);
    }
}
