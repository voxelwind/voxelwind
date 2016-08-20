package com.voxelwind.server.network.rcon;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteOrder;

public class RconEncoder extends MessageToByteEncoder<RconMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RconMessage message, ByteBuf buf) throws Exception {
        ByteBuf leBuf = buf.order(ByteOrder.LITTLE_ENDIAN);
        // 4 bytes ID, 4 bytes type, 1 byte terminating zero
        int fullLength = 9 + message.getBody().length();

        leBuf.writeInt(fullLength);
        leBuf.writeInt(message.getId());
        leBuf.writeInt(message.getType());
        ByteBufUtil.writeUtf8(leBuf, message.getBody());
        // null byte
        leBuf.writeByte(0);
    }
}
