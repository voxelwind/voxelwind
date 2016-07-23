package io.minimum.voxelwind.network.raknet.packets;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;

import static io.minimum.voxelwind.network.raknet.RakNetConstants.RAKNET_UNCONNECTED_MAGIC;

public class OpenConnectionRequest2Packet implements RakNetPackage {
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

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(InetSocketAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    public short getMtuSize() {
        return mtuSize;
    }

    public void setMtuSize(short mtuSize) {
        this.mtuSize = mtuSize;
    }
}
