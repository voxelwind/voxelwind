package com.voxelwind.server.network.rcon;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteOrder;
import java.util.List;

public class RconDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
        int id = buf.readIntLE();
        int type = buf.readIntLE();
        String body = readNullTerminatedString(buf);

        // Discard remaining bytes
        buf.readerIndex(buf.writerIndex());

        list.add(new RconMessage(id, type, body));
    }

    private String readNullTerminatedString(ByteBuf in) {
        StringBuilder read = new StringBuilder();
        byte readIn;
        while ((readIn = in.readByte()) != '\0') {
            read.append((char) readIn);
        }
        return read.toString();
    }
}
