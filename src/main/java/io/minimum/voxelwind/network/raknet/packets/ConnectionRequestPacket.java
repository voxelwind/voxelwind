package io.minimum.voxelwind.network.raknet.packets;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

public class ConnectionRequestPacket implements RakNetPackage {
    private long clientGuid;
    private long timestamp;

    @Override
    public void decode(ByteBuf buffer) {
        clientGuid = buffer.readLong();
        timestamp = buffer.readLong();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(clientGuid);
        buffer.writeLong(timestamp);
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
}
