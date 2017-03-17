package com.voxelwind.server.network.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

/**
 * A special version of {@link ByteBufOutputStream} that reverses endian order.
 */
public class LittleEndianByteBufOutputStream extends ByteBufOutputStream {
    public LittleEndianByteBufOutputStream(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void writeInt(int v) throws IOException {
        buffer().writeIntLE(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        buffer().writeLongLE(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        buffer().writeShortLE((short) v);
    }
}
