package com.voxelwind.server.network.raknet.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class ConnectionRequestPacket implements RakNetPackage {
    private long clientGuid;
    private long timestamp;
    private boolean serverSecurity;

    @Override
    public void decode(ByteBuf buffer) {
        clientGuid = buffer.readLong();
        timestamp = buffer.readLong();
        serverSecurity = buffer.readBoolean();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(clientGuid);
        buffer.writeLong(timestamp);
        buffer.writeBoolean(serverSecurity);
    }
}
