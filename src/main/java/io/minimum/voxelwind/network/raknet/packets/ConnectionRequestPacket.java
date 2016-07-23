package io.minimum.voxelwind.network.raknet.packets;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

public class ConnectionRequestPacket implements RakNetPackage {
    private long clientGuid;
    private long timestamp;
    private byte serverSecurity;

    @Override
    public void decode(ByteBuf buffer) {
        clientGuid = buffer.readLong();
        timestamp = buffer.readLong();
        serverSecurity = buffer.readByte();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(clientGuid);
        buffer.writeLong(timestamp);
        buffer.writeByte(serverSecurity);
    }

    public long getClientGuid() {
        return clientGuid;
    }

    public void setClientGuid(long clientGuid) {
        this.clientGuid = clientGuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte getServerSecurity() {
        return serverSecurity;
    }

    public void setServerSecurity(byte serverSecurity) {
        this.serverSecurity = serverSecurity;
    }
}
