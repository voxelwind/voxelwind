package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.level.util.Attribute;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.util.metadata.MetadataDictionary;
import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collection;

public class McpeAddEntity implements RakNetPackage {
    private long entityId;
    private int entityType;
    private Vector3f position;
    private Vector3f velocity;
    private float yaw;
    private float pitch;
    // TODO: Attributes and metadata.
    private final MetadataDictionary dictionary = new MetadataDictionary();
    private final Collection<Attribute> attributes = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        entityId = buffer.readLong();
        entityType = buffer.readInt();
        position = McpeUtil.readVector3f(buffer);
        velocity = McpeUtil.readVector3f(buffer);
        yaw = buffer.readByte();
        pitch = buffer.readFloat();
        dictionary.putAll(MetadataDictionary.deserialize(buffer));
        attributes.addAll(McpeUtil.readAttributes(buffer));
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(entityId);
        buffer.writeInt(entityType);
        McpeUtil.writeVector3f(buffer, position);
        McpeUtil.writeVector3f(buffer, velocity);
        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);
        dictionary.writeTo(buffer);
        McpeUtil.writeAttributes(buffer, attributes);
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public MetadataDictionary getDictionary() {
        return dictionary;
    }

    public Collection<Attribute> getAttributes() {
        return attributes;
    }
}
