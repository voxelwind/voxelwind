package io.minimum.voxelwind.network.raknet.packets;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;

import static io.minimum.voxelwind.network.raknet.RakNetConstants.RAKNET_UNCONNECTED_MAGIC;

public class OpenConnectionResponse1Packet implements RakNetPackage {
    private byte serverSecurity;
    private long serverGuid;
    private short mtuSize;

    @Override
    public void decode(ByteBuf buffer) {
        RakNetUtil.verifyUnconnectedMagic(buffer);
        serverGuid = buffer.readLong();
        serverSecurity = buffer.readByte();
        mtuSize = buffer.readShort();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBytes(RAKNET_UNCONNECTED_MAGIC);
        buffer.writeLong(serverGuid);
        buffer.writeByte(serverSecurity);
        buffer.writeShort(mtuSize);
    }

    public byte getServerSecurity() {
        return serverSecurity;
    }

    public void setServerSecurity(byte serverSecurity) {
        this.serverSecurity = serverSecurity;
    }

    public short getMtuSize() {
        return mtuSize;
    }

    public void setMtuSize(short mtuSize) {
        this.mtuSize = mtuSize;
    }

    public long getServerGuid() {
        return serverGuid;
    }

    public void setServerGuid(long serverGuid) {
        this.serverGuid = serverGuid;
    }
}
