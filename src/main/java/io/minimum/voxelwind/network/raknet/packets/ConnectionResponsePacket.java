package io.minimum.voxelwind.network.raknet.packets;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;

public class ConnectionResponsePacket implements RakNetPackage {
    private InetSocketAddress systemAddress;
    private int systemIndex;
    private InetSocketAddress[] systemAddresses;
    private long incomingTimestamp;
    private long systemTimestamp;

    @Override
    public void decode(ByteBuf buffer) {
        systemAddress = RakNetUtil.readSocketAddress(buffer);
        systemIndex = buffer.readInt();
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
        buffer.writeInt(systemIndex);
        for (InetSocketAddress address : systemAddresses) {
            RakNetUtil.writeSocketAddress(buffer, address);
        }
        buffer.writeLong(incomingTimestamp);
        buffer.writeLong(systemTimestamp);
    }

    public InetSocketAddress getSystemAddress() {
        return systemAddress;
    }

    public void setSystemAddress(InetSocketAddress systemAddress) {
        this.systemAddress = systemAddress;
    }

    public int getSystemIndex() {
        return systemIndex;
    }

    public void setSystemIndex(int systemIndex) {
        this.systemIndex = systemIndex;
    }

    public InetSocketAddress[] getSystemAddresses() {
        return systemAddresses;
    }

    public void setSystemAddresses(InetSocketAddress[] systemAddresses) {
        this.systemAddresses = systemAddresses;
    }

    public long getIncomingTimestamp() {
        return incomingTimestamp;
    }

    public void setIncomingTimestamp(long incomingTimestamp) {
        this.incomingTimestamp = incomingTimestamp;
    }

    public long getSystemTimestamp() {
        return systemTimestamp;
    }

    public void setSystemTimestamp(long systemTimestamp) {
        this.systemTimestamp = systemTimestamp;
    }
}
