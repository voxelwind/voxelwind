package io.minimum.voxelwind.network.raknet.packets;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;

import static io.minimum.voxelwind.network.raknet.RakNetConstants.RAKNET_UNCONNECTED_MAGIC;

public class OpenConnectionResponse2Packet implements RakNetPackage {
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

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public InetSocketAddress getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(InetSocketAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public short getMtuSize() {
        return mtuSize;
    }

    public void setMtuSize(short mtuSize) {
        this.mtuSize = mtuSize;
    }

    public byte getServerSecurity() {
        return serverSecurity;
    }

    public void setServerSecurity(byte serverSecurity) {
        this.serverSecurity = serverSecurity;
    }
}
