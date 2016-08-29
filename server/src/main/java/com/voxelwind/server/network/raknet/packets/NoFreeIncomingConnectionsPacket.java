package com.voxelwind.server.network.raknet.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.voxelwind.server.network.raknet.RakNetConstants.RAKNET_UNCONNECTED_MAGIC;

@Data
public class NoFreeIncomingConnectionsPacket implements NetworkPackage {
    private long serverGuid;

    @Override
    public void decode(ByteBuf buffer) {
        RakNetUtil.verifyUnconnectedMagic(buffer);
        serverGuid = buffer.readLong();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBytes(RAKNET_UNCONNECTED_MAGIC);
        buffer.writeLong(serverGuid);
    }
}
