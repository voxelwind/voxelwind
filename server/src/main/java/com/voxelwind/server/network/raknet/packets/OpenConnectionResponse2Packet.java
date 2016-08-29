package com.voxelwind.server.network.raknet.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.net.InetSocketAddress;

import static com.voxelwind.server.network.raknet.RakNetConstants.RAKNET_UNCONNECTED_MAGIC;

@Data
public class OpenConnectionResponse2Packet implements NetworkPackage {
    private long serverId;
    private InetSocketAddress clientAddress;
    private short mtuSize;
    private byte serverSecurity;

    @Override
    public void decode(ByteBuf buffer) {
        RakNetUtil.verifyUnconnectedMagic(buffer);
        serverId = buffer.readLong();
        clientAddress = RakNetUtil.readSocketAddress(buffer);
        mtuSize = buffer.readShort();
        serverSecurity = buffer.readByte();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBytes(RAKNET_UNCONNECTED_MAGIC);
        buffer.writeLong(serverId);
        RakNetUtil.writeSocketAddress(buffer, clientAddress);
        buffer.writeShort(mtuSize);
        buffer.writeByte(serverSecurity);
    }
}
