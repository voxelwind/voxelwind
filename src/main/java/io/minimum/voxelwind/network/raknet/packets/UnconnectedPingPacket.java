package io.minimum.voxelwind.network.raknet.packets;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;

import static io.minimum.voxelwind.network.raknet.RakNetConstants.RAKNET_UNCONNECTED_MAGIC;

public class UnconnectedPingPacket implements RakNetPackage {
    private long pingId;
    private long serverId;

    @Override
    public void decode(ByteBuf buffer) {
        pingId = buffer.readLong();
        RakNetUtil.verifyUnconnectedMagic(buffer);
        serverId = buffer.readLong();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(pingId);
        buffer.writeBytes(RAKNET_UNCONNECTED_MAGIC);
        buffer.writeLong(serverId);
    }

    public long getPingId() {
        return pingId;
    }

    public void setPingId(long pingId) {
        this.pingId = pingId;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }
}
