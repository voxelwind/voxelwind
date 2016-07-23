package io.minimum.voxelwind.network.handler;

import io.minimum.voxelwind.VoxelwindServer;
import io.minimum.voxelwind.network.raknet.enveloped.DirectAddressedRakNetPacket;
import io.minimum.voxelwind.network.raknet.packets.*;
import io.minimum.voxelwind.network.session.UserSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.util.Arrays;

public class VoxelwindDirectPacketHandler extends SimpleChannelInboundHandler<DirectAddressedRakNetPacket> {
    private final VoxelwindServer server;

    public VoxelwindDirectPacketHandler(VoxelwindServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DirectAddressedRakNetPacket packet) throws Exception {
        // ** Everything we can handle without a session **
        if (packet.content() instanceof UnconnectedPingPacket) {
            UnconnectedPingPacket request = (UnconnectedPingPacket) packet.content();
            UnconnectedPongPacket response = new UnconnectedPongPacket();
            response.setPingId(request.getPingId());
            response.setServerId(68382);
            response.setAdvertise("MCPE;Voxelwind server;81;0.15.0;" + server.getSessionManager().countConnected() + ";10000");
            ctx.writeAndFlush(new DirectAddressedRakNetPacket(response, packet.sender(), packet.recipient()));
            return;
        }

        // ** Everything requiring a session **
        UserSession session = server.getSessionManager().get(packet.sender());

        if (session == null)
            return;

        if (packet.content() instanceof AckPacket) {
            session.onAck(((AckPacket) packet.content()).getIds());
        }
        if (packet.content() instanceof NakPacket) {
            session.onNak(((NakPacket) packet.content()).getIds());
        }
        if (packet.content() instanceof ConnectionRequestPacket) {
            ConnectionRequestPacket request = (ConnectionRequestPacket) packet.content();
            ConnectionResponsePacket response = new ConnectionResponsePacket();
            response.setIncomingTimestamp(request.getTimestamp());
            response.setSystemTimestamp(System.currentTimeMillis());
            response.setSystemAddress(InetAddress.getLoopbackAddress());
            InetAddress[] addresses = new InetAddress[10];
            Arrays.fill(addresses, InetAddress.getLoopbackAddress());
            response.setSystemAddresses(addresses);
            response.setSystemIndex(0);
            session.sendUrgentPackage(response);
            return;
        }
        if (packet.content() instanceof ConnectedPingPacket) {
            ConnectedPingPacket request = (ConnectedPingPacket) packet.content();
            ConnectedPongPacket response = new ConnectedPongPacket();
            response.setPingTime(request.getPingTime());
            response.setPongTime(System.currentTimeMillis());
            session.sendUrgentPackage(response);
            return;
        }
    }
}
