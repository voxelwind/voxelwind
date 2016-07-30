package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

public class McpeClientMagic implements RakNetPackage {
    @Override
    public void decode(ByteBuf buffer) {
        buffer.skipBytes(buffer.readableBytes());
    }

    @Override
    public void encode(ByteBuf buffer) {

    }
}
