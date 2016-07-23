package io.minimum.voxelwind.network.raknet.packets;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import static io.minimum.voxelwind.network.raknet.RakNetConstants.RAKNET_UNCONNECTED_MAGIC;

public class UnconnectedPongPacket implements RakNetPackage {
    private long pingId;
    private long serverId;
    private String advertise;

    @Override
    public void decode(ByteBuf buffer) {
        pingId = buffer.readLong();
        serverId = buffer.readLong();
        RakNetUtil.verifyUnconnectedMagic(buffer);
        advertise = RakNetUtil.readString(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(pingId);
        buffer.writeLong(serverId);
        buffer.writeBytes(RAKNET_UNCONNECTED_MAGIC);
        RakNetUtil.writeString(buffer, advertise);
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

    public String getAdvertise() {
        return advertise;
    }

    public void setAdvertise(String advertise) {
        this.advertise = advertise;
    }
}
