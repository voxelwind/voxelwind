package io.minimum.voxelwind.network.raknet.packets;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

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

    public boolean isServerSecurity() {
        return serverSecurity;
    }

    public void setServerSecurity(boolean serverSecurity) {
        this.serverSecurity = serverSecurity;
    }
}
