package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeChangeDimension implements NetworkPackage {
    private byte dimension;
    private Vector3f position;
    private byte unknown;

    @Override
    public void decode(ByteBuf buffer) {
        dimension = buffer.readByte();
        position = McpeUtil.readVector3f(buffer);
        unknown = buffer.readByte();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(dimension);
        McpeUtil.writeVector3f(buffer, position);
        buffer.writeByte(unknown);
    }
}
