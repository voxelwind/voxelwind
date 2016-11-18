package com.voxelwind.server.network.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import static org.junit.Assert.*;

public class LittleEndianByteBufOutputStreamTest {
    @Test
    public void verifyOutput() throws Exception {
        ByteBuf buf = Unpooled.buffer();
        try {
            LittleEndianByteBufOutputStream stream = new LittleEndianByteBufOutputStream(buf);
            stream.writeByte(0x7f);
            stream.writeShort(28501);
            stream.writeInt(5870106);
            stream.writeLong(93957294581L);

            assertEquals(0x7f, buf.readByte());
            assertEquals(28501, buf.readShortLE());
            assertEquals(5870106, buf.readIntLE());
            assertEquals(93957294581L, buf.readLongLE());
        } finally {
            buf.release();
        }
    }
}