package com.voxelwind.server.network.raknet.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.voxelwind.server.network.raknet.RakNetConstants.RAKNET_UNCONNECTED_MAGIC;

@Data
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
}
