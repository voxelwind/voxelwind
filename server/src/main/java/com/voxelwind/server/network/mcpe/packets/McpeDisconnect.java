package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeDisconnect implements RakNetPackage {
    private String message;

    @Override
    public void decode(ByteBuf buffer) {
        message = RakNetUtil.readString(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        RakNetUtil.writeString(buffer, message);
    }
}
