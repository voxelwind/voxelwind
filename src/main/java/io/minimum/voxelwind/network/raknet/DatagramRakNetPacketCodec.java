package io.minimum.voxelwind.network.raknet;

import io.minimum.voxelwind.VoxelwindServer;
import io.minimum.voxelwind.network.raknet.datagrams.RakNetDatagram;
import io.minimum.voxelwind.network.raknet.enveloped.AddressedRakNetDatagram;
import io.minimum.voxelwind.network.session.UserSession;
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

        RakNetDatagram datagram = new RakNetDatagram();
        datagram.decode(packet.content());
        list.add(datagram);
    }
}
