package com.voxelwind.server.network.raknet.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.net.InetSocketAddress;

import static com.voxelwind.server.network.raknet.RakNetConstants.RAKNET_UNCONNECTED_MAGIC;

@Data
public class OpenConnectionRequest2Packet implements NetworkPackage {
    private long clientId;
    private InetSocketAddress serverAddress;
    private short mtuSize;

    @Override
    public void decode(ByteBuf buffer) {
        RakNetUtil.verifyUnconnectedMagic(buffer);
        serverAddress = RakNetUtil.readSocketAddress(buffer);
        mtuSize = buffer.readShort();
        clientId = buffer.readLong();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBytes(RAKNET_UNCONNECTED_MAGIC);
        RakNetUtil.writeSocketAddress(buffer, serverAddress);
        buffer.writeShort(mtuSize);
        buffer.writeLong(clientId);
    }
}
