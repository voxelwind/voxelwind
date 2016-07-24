package io.minimum.voxelwind.network.raknet;

import io.minimum.voxelwind.VoxelwindServer;
import io.minimum.voxelwind.network.raknet.datagrams.EncapsulatedRakNetPacket;
import io.minimum.voxelwind.network.raknet.datagrams.RakNetDatagram;
import io.minimum.voxelwind.network.raknet.datagrams.RakNetDatagramFlags;
import io.minimum.voxelwind.network.raknet.enveloped.AddressedRakNetDatagram;
import io.minimum.voxelwind.network.session.UserSession;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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
        System.out.println("[Sent] " + datagram + "\n" + ByteBufUtil.prettyHexDump(buf));
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
            datagram.decode(packet.content().retain()); // Must be retained since packet body is a slice

            System.out.println("[RakNet Datagram] " + datagram);
            for (EncapsulatedRakNetPacket packet1 : datagram.getPackets()) {
                System.out.println("[Encapsulated Packet] " + packet + ":\n" + ByteBufUtil.prettyHexDump(packet1.getBuffer()));
            }
            list.add(new AddressedRakNetDatagram(datagram, packet.recipient(), packet.sender()));
        }
    }
}
