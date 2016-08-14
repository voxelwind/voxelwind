package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class McpeUnknown implements RakNetPackage {
    private short id;
    private ByteBuf buf;

    @Override
    public void decode(ByteBuf buffer) {
        id = buffer.readUnsignedByte();
        buf = buffer.readBytes(buffer.readableBytes());
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeShort(id);
        buffer.writeBytes(buf);
    }

    @Override
    public String toString() {
        return "UNKNOWN - " + Integer.toHexString(id) + " - Hex: " + ByteBufUtil.hexDump(buf);
    }

    public short getId() {
        return id;
    }

    public ByteBuf getBuf() {
        return buf;
    }
}
