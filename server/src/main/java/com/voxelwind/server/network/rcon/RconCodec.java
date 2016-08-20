package com.voxelwind.server.network.rcon;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.nio.ByteOrder;
import java.util.List;

public class RconCodec extends ByteToMessageCodec<RconMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RconMessage message, ByteBuf buf) throws Exception {
        ByteBuf leBuf = buf.order(ByteOrder.LITTLE_ENDIAN);
        leBuf.writeInt(message.getId());
        leBuf.writeInt(message.getType());
        ByteBufUtil.writeUtf8(leBuf, message.getBody());
        // Two nulls:
        leBuf.writeByte(0);
        leBuf.writeByte(0);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
        ByteBuf leBuf = buf.order(ByteOrder.LITTLE_ENDIAN);
        int id = leBuf.readInt();
        int type = leBuf.readInt();
        String body = readNullTerminatedString(buf);
        // Discard a null byte
        buf.readByte();

        list.add(new RconMessage(id, type, body));
    }

    private String readNullTerminatedString(ByteBuf in) {
        StringBuilder read = new StringBuilder();
        char readIn;
        while ((readIn = in.readChar()) != '\0') {
            read.append(readIn);
        }
        return read.toString();
    }
}
