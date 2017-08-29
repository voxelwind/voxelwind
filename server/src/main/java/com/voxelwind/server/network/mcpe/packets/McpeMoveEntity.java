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
    private boolean onGround;
    private boolean teleported;

    @Override
    public void decode(ByteBuf buffer) {
        entityId = Varints.decodeUnsigned(buffer);
        position = McpeUtil.readVector3f(buffer);
        rotation = McpeUtil.readByteRotation(buffer);
        onGround = buffer.readBoolean();
        teleported = buffer.readBoolean();
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeUnsigned(buffer, entityId);
        McpeUtil.writeVector3f(buffer, position);
        McpeUtil.writeByteRotation(buffer, rotation);
        buffer.writeBoolean(onGround);
        buffer.writeBoolean(teleported);
    }
}
