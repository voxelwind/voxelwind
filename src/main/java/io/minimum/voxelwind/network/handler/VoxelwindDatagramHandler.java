package io.minimum.voxelwind.network.handler;

import io.minimum.voxelwind.VoxelwindServer;
import io.minimum.voxelwind.network.mcpe.packets.McpeBatch;
import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.datagrams.RakNetDatagramFlags;
import io.minimum.voxelwind.network.raknet.enveloped.AddressedRakNetDatagram;
import io.minimum.voxelwind.network.raknet.packets.AckPacket;
import io.minimum.voxelwind.network.session.UserSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class VoxelwindDatagramHandler extends SimpleChannelInboundHandler<AddressedRakNetDatagram> {
    private final VoxelwindServer server;

    public VoxelwindDatagramHandler(VoxelwindServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AddressedRakNetDatagram datagram) throws Exception {
        UserSession session = server.getSessionManager().get(datagram.sender());

        if (session == null)
            return;

        // First, acknowledge receipt of the datagram.
        session.enqueueAck(datagram.content().getDatagramSequenceNumber());

        // Now figure out the underlying packets.
        RakNetDatagramFlags flags = datagram.content().getFlags();
        if (flags.isAck() && flags.isValid()) {
            // ACK: acknowledged receipt of one of our datagrams.
            AckPacket packet = new AckPacket();
            packet.decode(datagram.content().getPackets().get(0).getBuffer());
            session.onAck(packet.getIds());
        }
    }

    private void handlePackage(RakNetPackage netPackage, UserSession session) throws Exception {
        // Special cases we need to handle in DatagramHandler.
        if (netPackage instanceof McpeBatch) {
            for (RakNetPackage aPackage : ((McpeBatch) netPackage).getPackages()) {
                handlePackage(aPackage, session);
            }
            return;
        }


    }
}
