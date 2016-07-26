package com.voxelwind.server.network.handler;

import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.network.raknet.enveloped.DirectAddressedRakNetPacket;
import com.voxelwind.server.network.raknet.packets.*;
import com.voxelwind.server.network.session.InitialNetworkPacketHandler;
import com.voxelwind.server.network.session.UserSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class VoxelwindDirectPacketHandler extends SimpleChannelInboundHandler<DirectAddressedRakNetPacket> {
    private static final long SERVER_ID = 68382;
    private final VoxelwindServer server;

    public VoxelwindDirectPacketHandler(VoxelwindServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DirectAddressedRakNetPacket packet) throws Exception {
        UserSession session = server.getSessionManager().get(packet.sender());

        // ** Everything we can handle without a session **
        if (packet.content() instanceof UnconnectedPingPacket) {
            UnconnectedPingPacket request = (UnconnectedPingPacket) packet.content();
            UnconnectedPongPacket response = new UnconnectedPongPacket();
            response.setPingId(request.getPingId());
            response.setServerId(SERVER_ID);
            response.setAdvertise("MCPE;Voxelwind server;81;0.15.0;" + server.getSessionManager().countConnected() + ";10000");
            ctx.writeAndFlush(new DirectAddressedRakNetPacket(response, packet.sender(), packet.recipient()));
            return;
        }
        if (packet.content() instanceof OpenConnectionRequest1Packet) {
            OpenConnectionRequest1Packet request = (OpenConnectionRequest1Packet) packet.content();
            OpenConnectionResponse1Packet response = new OpenConnectionResponse1Packet();
            response.setMtuSize(request.getMtu());
            response.setServerSecurity((byte) 0);
            response.setServerGuid(SERVER_ID);
            ctx.writeAndFlush(new DirectAddressedRakNetPacket(response, packet.sender(), packet.recipient()));
            return;
        }
        if (packet.content() instanceof OpenConnectionRequest2Packet) {
            OpenConnectionRequest2Packet request = (OpenConnectionRequest2Packet) packet.content();
            OpenConnectionResponse2Packet response = new OpenConnectionResponse2Packet();
            response.setMtuSize(request.getMtuSize());
            response.setServerSecurity((byte) 0);
            response.setClientAddress(packet.sender());
            response.setServerId(SERVER_ID);
            session = new UserSession(packet.sender(), request.getMtuSize(), null, ctx.channel(), server);
            session.setHandler(new InitialNetworkPacketHandler(session));
            server.getSessionManager().add(packet.sender(), session);
            ctx.writeAndFlush(new DirectAddressedRakNetPacket(response, packet.sender(), packet.recipient()));
            return;
        }

        // ** Everything requiring a session **
        if (session == null)
            return;

        if (packet.content() instanceof AckPacket) {
            session.onAck(((AckPacket) packet.content()).getIds());
        }
        if (packet.content() instanceof NakPacket) {
            session.onNak(((NakPacket) packet.content()).getIds());
        }
    }
}
