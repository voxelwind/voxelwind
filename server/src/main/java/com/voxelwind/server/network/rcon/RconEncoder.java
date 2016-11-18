package com.voxelwind.server.network.rcon;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RconEncoder extends MessageToByteEncoder<RconMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RconMessage message, ByteBuf buf) throws Exception {
        buf.writeIntLE(message.getId());
        buf.writeIntLE(message.getType());
        ByteBufUtil.writeAscii(buf, message.getBody());
        // 2 null bytes
        buf.writeByte(0);
        buf.writeByte(0);
    }
}
