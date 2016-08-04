package com.voxelwind.server.network.raknet;

import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.network.raknet.datagrams.RakNetDatagram;
import com.voxelwind.server.network.raknet.datagrams.RakNetDatagramFlags;
import com.voxelwind.server.network.raknet.enveloped.AddressedRakNetDatagram;
import com.voxelwind.server.network.session.UserSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

public class DatagramRakNetPacketCodec extends MessageToMessageCodec<DatagramPacket, AddressedRakNetDatagram> {
    private final VoxelwindServer server;

    public DatagramRakNetPacketCodec(VoxelwindServer server) {
        this.server = server;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, AddressedRakNetDatagram datagram, List<Object> list) throws Exception {
        ByteBuf buf = ctx.alloc().buffer();
        datagram.content().encode(buf);
        list.add(new DatagramPacket(buf, datagram.recipient(), datagram.sender()));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> list) throws Exception {
        // Requires a session
        UserSession session = server.getSessionManager().get(packet.sender());

        if (session == null)
            return;

        packet.content().markReaderIndex();
        RakNetDatagramFlags flags = new RakNetDatagramFlags(packet.content().readByte());
        packet.content().resetReaderIndex();

        if (flags.isValid() && !flags.isAck() && !flags.isNak()) {
            //System.out.println("[RakNet Datagram] " + packet + ":\n " + ByteBufUtil.prettyHexDump(packet.content()));
            RakNetDatagram datagram = new RakNetDatagram();
            datagram.decode(packet.content().retain()); // Must be retained since packet bodies are slices
            list.add(new AddressedRakNetDatagram(datagram, packet.recipient(), packet.sender()));
        }
    }
}
