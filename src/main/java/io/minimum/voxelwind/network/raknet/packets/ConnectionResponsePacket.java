package io.minimum.voxelwind.network.raknet.packets;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;

import java.net.InetAddress;

public class ConnectionResponsePacket implements RakNetPackage {
    private InetAddress systemAddress;
    private int systemIndex;
    private InetAddress[] systemAddresses;
    private long incomingTimestamp;
    private long systemTimestamp;

    @Override
    public void decode(ByteBuf buffer) {
        systemAddress = RakNetUtil.readAddress(buffer);
        systemIndex = buffer.readInt();
        systemAddresses = new InetAddress[10];
        for (int i = 0; i < 10; i++) {
            systemAddresses[i] = RakNetUtil.readAddress(buffer);
        }
        incomingTimestamp = buffer.readLong();
        systemTimestamp = buffer.readLong();
    }

    @Override
    public void encode(ByteBuf buffer) {
        RakNetUtil.writeAddress(buffer, systemAddress);
        buffer.writeInt(systemIndex);
        for (InetAddress address : systemAddresses) {
            RakNetUtil.writeAddress(buffer, address);
        }
        buffer.writeLong(incomingTimestamp);
        buffer.writeLong(systemTimestamp);
    }

    public InetAddress getSystemAddress() {
        return systemAddress;
    }

    public void setSystemAddress(InetAddress systemAddress) {
        this.systemAddress = systemAddress;
    }

    public int getSystemIndex() {
        return systemIndex;
    }

    public void setSystemIndex(int systemIndex) {
        this.systemIndex = systemIndex;
    }

    public InetAddress[] getSystemAddresses() {
        return systemAddresses;
    }

    public void setSystemAddresses(InetAddress[] systemAddresses) {
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
