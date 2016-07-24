package io.minimum.voxelwind.network.mcpe.packets;

import io.minimum.voxelwind.network.raknet.RakNetPackage;
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
