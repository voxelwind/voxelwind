package com.voxelwind.server.network.rcon;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

public class RconEncodeDecodeTest {
    @Test
    public void decodeTest() throws Exception {
        ByteBuf buf = Unpooled.wrappedBuffer(DatatypeConverter.parseHexBinary("00000000030000006d7920766f696365206973206d792070617373706f72740000"));
        EmbeddedChannel channel = new EmbeddedChannel(new RconDecoder());
        try {
            channel.writeInbound(buf);
            RconMessage message = (RconMessage) channel.readInbound();
            RconMessage intended = new RconMessage(0, 3, "my voice is my passport");
            Assert.assertEquals("Read message is invalid.", intended, message);
        } finally {
            channel.close();
        }
    }

    @Test
    public void encodeTest() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new RconEncoder());
        ByteBuf expected = Unpooled.wrappedBuffer(DatatypeConverter.parseHexBinary("00000000030000006d7920766f696365206973206d792070617373706f72740000"));
        try {
            channel.writeOutbound(new RconMessage(0, 3, "my voice is my passport"));
            ByteBuf buf = (ByteBuf) channel.readOutbound();
            Assert.assertEquals("Read message is invalid.", expected, buf);
        } finally {
            expected.release();
            channel.close();
        }
    }
}
