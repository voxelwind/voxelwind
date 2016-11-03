package com.voxelwind.server.network.raknet.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.raknet.RakNetConstants;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class OpenConnectionResponse1Packet implements NetworkPackage {
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
        buffer.writeBytes(RakNetConstants.RAKNET_UNCONNECTED_MAGIC);
        buffer.writeLong(serverGuid);
        buffer.writeByte(serverSecurity);
        buffer.writeShort(mtuSize);
    }
}
