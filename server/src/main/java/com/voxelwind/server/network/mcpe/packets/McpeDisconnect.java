package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
