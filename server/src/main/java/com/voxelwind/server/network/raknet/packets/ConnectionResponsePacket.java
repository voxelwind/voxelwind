package com.voxelwind.server.network.raknet.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.annotations.DisallowWrapping;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.net.InetSocketAddress;

@DisallowWrapping
@Data
public class ConnectionResponsePacket implements NetworkPackage {
    private InetSocketAddress systemAddress;
    private short systemIndex;
    private InetSocketAddress[] systemAddresses;
    private long incomingTimestamp;
    private long systemTimestamp;

    @Override
    public void decode(ByteBuf buffer) {
        systemAddress = RakNetUtil.readSocketAddress(buffer);
        systemIndex = buffer.readShort();
        systemAddresses = new InetSocketAddress[10];
        for (int i = 0; i < 10; i++) {
            systemAddresses[i] = RakNetUtil.readSocketAddress(buffer);
        }
        incomingTimestamp = buffer.readLong();
        systemTimestamp = buffer.readLong();
    }

    @Override
    public void encode(ByteBuf buffer) {
        RakNetUtil.writeSocketAddress(buffer, systemAddress);
        buffer.writeShort(systemIndex);
        for (InetSocketAddress address : systemAddresses) {
            RakNetUtil.writeSocketAddress(buffer, address);
        }
        buffer.writeLong(incomingTimestamp);
        buffer.writeLong(systemTimestamp);
    }
}
