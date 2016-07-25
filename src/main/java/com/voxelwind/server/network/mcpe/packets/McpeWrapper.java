package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

public class McpeWrapper implements RakNetPackage {
    private ByteBuf wrapped;

    @Override
    public void decode(ByteBuf buffer) {
        wrapped = buffer.readSlice(buffer.readableBytes());
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBytes(wrapped);
    }

    public ByteBuf getWrapped() {
        return wrapped;
    }

    public void setWrapped(ByteBuf wrapped) {
        this.wrapped = wrapped;
    }
}
