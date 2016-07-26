package com.voxelwind.server.network.util;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static org.junit.Assert.*;

public class CompressionUtilTest {
    @org.junit.Test
    public void fullTest() throws Exception {
        ByteBuf toCompress = Unpooled.directBuffer();
        toCompress.writeBytes("Voxelwind test".getBytes(Charsets.UTF_8));

        ByteBuf asCompressed = CompressionUtil.deflate(toCompress);
        ByteBuf asUncompressed = CompressionUtil.inflate(asCompressed);

        // Reader index will be incorrect. This is intentional, so fix it.
        toCompress.readerIndex(0);

        assertEquals("Data did not properly decompress", toCompress, asUncompressed);

        toCompress.release();
        asCompressed.release();
        asUncompressed.release();
    }
}