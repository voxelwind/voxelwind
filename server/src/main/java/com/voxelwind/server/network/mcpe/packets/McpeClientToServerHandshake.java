package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;

public class McpeClientToServerHandshake implements NetworkPackage {
    @Override
    public void decode(ByteBuf buffer) {
        buffer.skipBytes(buffer.readableBytes());
    }

    @Override
    public void encode(ByteBuf buffer) {

    }
}
