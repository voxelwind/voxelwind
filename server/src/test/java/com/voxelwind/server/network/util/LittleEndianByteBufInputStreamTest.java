package com.voxelwind.server.network.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import static org.junit.Assert.*;

public class LittleEndianByteBufInputStreamTest {
    @Test
    public void verifyInput() throws Exception {
        ByteBuf buf = Unpooled.buffer();
        try {
            buf.writeByte(0x7f);
            buf.writeShortLE(28501);
            buf.writeIntLE(5870106);
            buf.writeLongLE(93957294581L);

            LittleEndianByteBufInputStream s = new LittleEndianByteBufInputStream(buf);
            assertEquals(0x7f, s.readByte());
            assertEquals(28501, s.readShort());
            assertEquals(5870106, s.readInt());
            assertEquals(93957294581L, s.readLong());
        } finally {
            buf.release();
        }
    }
}