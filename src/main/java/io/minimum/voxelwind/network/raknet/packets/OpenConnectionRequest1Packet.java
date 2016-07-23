package io.minimum.voxelwind.network.raknet.packets;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;

import static io.minimum.voxelwind.network.raknet.RakNetConstants.RAKNET_UNCONNECTED_MAGIC;

public class OpenConnectionRequest1Packet implements RakNetPackage {
    private byte protocolVersion;
    private short mtu;

    @Override
    public void decode(ByteBuf buffer) {
        RakNetUtil.verifyUnconnectedMagic(buffer);
        protocolVersion = buffer.readByte();
        mtu = (short) (buffer.readableBytes() + 18);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBytes(RAKNET_UNCONNECTED_MAGIC);
        buffer.writeByte(protocolVersion);
        buffer.writeBytes(new byte[mtu - 18]);
    }

    public byte getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public short getMtu() {
        return mtu;
    }

    public void setMtu(short mtu) {
        this.mtu = mtu;
    }
}
