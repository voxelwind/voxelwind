package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import com.voxelwind.server.game.level.util.Attribute;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.util.metadata.MetadataDictionary;
import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class McpeAddPlayer implements RakNetPackage {
    //uuid	uuid
    //username	string
    //entity ID	long
    //position	vector
    //velocity	vector
    //yaw	float
    //head yaw	float
    //pitch	float
    //held item	item stack
    //meta	entity meta

    private UUID uuid;
    private String username;
    private long entityId;
    private Vector3f position;
    private Vector3f velocity;
    private float yaw;
    private float pitch;
    private ItemStack held = new VoxelwindItemStack(BlockTypes.AIR, 1, null);
    private final MetadataDictionary metadata = new MetadataDictionary();

    @Override
    public void decode(ByteBuf buffer) {
        uuid = McpeUtil.readUuid(buffer);
        username = RakNetUtil.readString(buffer);
        entityId = buffer.readLong();
        position = McpeUtil.readVector3f(buffer);
        velocity = McpeUtil.readVector3f(buffer);
        yaw = buffer.readFloat();
        pitch = buffer.readFloat();
        held = McpeUtil.readItemStack(buffer);
        metadata.putAll(MetadataDictionary.deserialize(buffer));
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeUuid(buffer, uuid);
        RakNetUtil.writeString(buffer, username);
        buffer.writeLong(entityId);
        McpeUtil.writeVector3f(buffer, position);
        McpeUtil.writeVector3f(buffer, velocity);
        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);
        McpeUtil.writeItemStack(buffer, held);
        metadata.writeTo(buffer);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public ItemStack getHeld() {
        return held;
    }

    public void setHeld(ItemStack held) {
        this.held = held;
    }

    public MetadataDictionary getMetadata() {
        return metadata;
    }
}
