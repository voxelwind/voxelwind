package com.voxelwind.server.network.raknet.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class ConnectedPingPacket implements RakNetPackage {
    private long pingTime;

    @Override
    public void decode(ByteBuf buffer) {
        pingTime = buffer.readLong();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(pingTime);
    }
}
