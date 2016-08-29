package com.voxelwind.server.network.raknet.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class NewIncomingConnectionPacket implements NetworkPackage {
    private InetSocketAddress clientAddress;
    private InetSocketAddress[] systemAddresses;
    private long clientTimestamp;
    private long serverTimestamp;

    @Override
    public void decode(ByteBuf buffer) {
        clientAddress = RakNetUtil.readSocketAddress(buffer);
        systemAddresses = new InetSocketAddress[10];
        for (int i = 0; i < 10; i++) {
            systemAddresses[i] = RakNetUtil.readSocketAddress(buffer);
        }
        clientTimestamp = buffer.readLong();
        serverTimestamp = buffer.readLong();
    }

    @Override
    public void encode(ByteBuf buffer) {
        RakNetUtil.writeSocketAddress(buffer, clientAddress);
        for (InetSocketAddress address : systemAddresses) {
            RakNetUtil.writeSocketAddress(buffer, address);
        }
        buffer.writeLong(clientTimestamp);
        buffer.writeLong(serverTimestamp);
    }
}
