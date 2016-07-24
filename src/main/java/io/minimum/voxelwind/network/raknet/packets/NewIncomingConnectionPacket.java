package io.minimum.voxelwind.network.raknet.packets;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class NewIncomingConnectionPacket implements RakNetPackage {
    private InetSocketAddress clientAddress;
    private InetSocketAddress[] systemAddresses;
    private long clientTimestamp;
    private long serverTimestamp;

    public InetSocketAddress getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(InetSocketAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public InetSocketAddress[] getSystemAddresses() {
        return systemAddresses;
    }

    public void setSystemAddresses(InetSocketAddress[] systemAddresses) {
        this.systemAddresses = systemAddresses;
    }

    public long getClientTimestamp() {
        return clientTimestamp;
    }

    public void setClientTimestamp(long clientTimestamp) {
        this.clientTimestamp = clientTimestamp;
    }

    public long getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(long serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

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
