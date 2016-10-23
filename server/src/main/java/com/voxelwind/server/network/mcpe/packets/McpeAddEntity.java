package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.game.level.util.Attribute;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.util.metadata.MetadataDictionary;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class McpeAddEntity implements NetworkPackage {
    private long entityId;
    private int entityType;
    private Vector3f position;
    private Vector3f velocity;
    private float yaw;
    private float pitch;
    private int modifiers;
    private final MetadataDictionary metadata = new MetadataDictionary();

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSignedLong(buffer, entityId);
        Varints.encodeSignedLong(buffer, entityId);
        Varints.encodeUnsigned(buffer, entityType);
        McpeUtil.writeVector3f(buffer, position);
        McpeUtil.writeVector3f(buffer, velocity);
        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);
        Varints.encodeUnsigned(buffer, modifiers);
        metadata.writeTo(buffer);
        Varints.encodeUnsigned(buffer, 0); // links, todo
    }
}
