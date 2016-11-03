package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.util.Rotation;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeMoveEntity implements NetworkPackage {
    private long entityId;
    private Vector3f position;
    private Rotation rotation;

    @Override
    public void decode(ByteBuf buffer) {
        entityId = Varints.decodeSignedLong(buffer);
        position = McpeUtil.readVector3f(buffer);
        rotation = McpeUtil.readByteRotation(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSignedLong(buffer, entityId);
        McpeUtil.writeVector3f(buffer, position);
        McpeUtil.writeByteRotation(buffer, rotation);
    }
}
