package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeRespawn implements NetworkPackage {
    private Vector3f position;

    @Override
    public void decode(ByteBuf buffer) {
        position = McpeUtil.readVector3f(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeVector3f(buffer, position);
    }
}
