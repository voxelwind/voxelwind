package io.minimum.voxelwind.network.raknet;

import io.minimum.voxelwind.network.PacketRegistry;
import io.minimum.voxelwind.network.PacketType;
import io.minimum.voxelwind.network.raknet.datagrams.RakNetDatagramFlags;
import io.minimum.voxelwind.network.raknet.enveloped.DirectAddressedRakNetPacket;
import io.minimum.voxelwind.network.raknet.packets.AckPacket;
import io.minimum.voxelwind.network.raknet.packets.NakPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

public class SimpleRakNetPacketCodec extends MessageToMessageCodec<DatagramPacket, DirectAddressedRakNetPacket> {
    private static final int USER_ID_START = 0x80;

    @Override
    protected void encode(ChannelHandlerContext ctx, DirectAddressedRakNetPacket pkg, List<Object> list) throws Exception {
        // Certain RakNet packets do not require special encapsulation. This encoder tries to handle them.
        Integer id = PacketRegistry.getId(pkg.content(), PacketType.RAKNET);
        if (id != null && id < USER_ID_START) {
            ByteBuf buf = PacketRegistry.tryEncode(pkg.content(), PacketType.RAKNET);
            list.add(new DatagramPacket(buf, pkg.recipient(), pkg.sender()));
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> list) throws Exception {
        // Certain RakNet packets do not require special encapsulation. This encoder tries to handle them.
        ByteBuf buf = packet.content();
        buf.markReaderIndex();
        int id = buf.readByte();
        if (id < USER_ID_START) { // User data
            buf.resetReaderIndex();

            // We can decode a packet immediately.
            RakNetPackage netPackage = PacketRegistry.tryDecode(buf, PacketType.RAKNET);
            if (netPackage != null) {
                list.add(new DirectAddressedRakNetPacket(netPackage, packet.recipient(), packet.sender()));
            }
        } else {
            // We can decode some datagrams directly.
            buf.resetReaderIndex();
            RakNetDatagramFlags flags = new RakNetDatagramFlags(buf.readByte());
            if (flags.isValid()) {
                if (flags.isAck()) {
                    // ACK
                    AckPacket ackPacket = new AckPacket();
                    ackPacket.decode(buf);
                    list.add(new DirectAddressedRakNetPacket(ackPacket, packet.recipient(), packet.sender()));
                } else if (flags.isNak()) {
                    // NAK
                    NakPacket nakPacket = new NakPacket();
                    nakPacket.decode(buf);
                    list.add(new DirectAddressedRakNetPacket(nakPacket, packet.recipient(), packet.sender()));
                }
            } else {
                buf.readerIndex(0);
            }
        }
    }
}
