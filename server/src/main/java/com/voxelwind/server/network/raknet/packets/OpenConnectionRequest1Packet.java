package com.voxelwind.server.network.raknet.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.voxelwind.server.network.raknet.RakNetConstants.RAKNET_UNCONNECTED_MAGIC;

@Data
public class OpenConnectionRequest1Packet implements NetworkPackage {
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
}
