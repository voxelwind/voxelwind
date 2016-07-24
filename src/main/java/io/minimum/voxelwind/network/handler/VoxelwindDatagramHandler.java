package io.minimum.voxelwind.network.handler;

import io.minimum.voxelwind.VoxelwindServer;
import io.minimum.voxelwind.network.PacketRegistry;
import io.minimum.voxelwind.network.PacketType;
import io.minimum.voxelwind.network.mcpe.packets.McpeBatch;
import io.minimum.voxelwind.network.mcpe.packets.McpeLogin;
import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.datagrams.EncapsulatedRakNetPacket;
import io.minimum.voxelwind.network.raknet.enveloped.AddressedRakNetDatagram;
import io.minimum.voxelwind.network.raknet.packets.*;
import io.minimum.voxelwind.network.session.UserSession;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Optional;

public class VoxelwindDatagramHandler extends SimpleChannelInboundHandler<AddressedRakNetDatagram> {
    private final VoxelwindServer server;
    private static final Logger LOGGER = LogManager.getLogger(VoxelwindDatagramHandler.class);

    public VoxelwindDatagramHandler(VoxelwindServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AddressedRakNetDatagram datagram) throws Exception {
        System.out.println(datagram);

        UserSession session = server.getSessionManager().get(datagram.sender());

        if (session == null)
            return;

        // Acknowledge receipt of the datagram.
        session.enqueueAck(datagram.content().getDatagramSequenceNumber());

        // Check the datagram contents.
        if (datagram.content().getFlags().isValid()) {
            System.out.println("[RakNet Datagram] " + datagram);
            for (EncapsulatedRakNetPacket packet : datagram.content().getPackets()) {
                System.out.println("[Encapsulated Packet] " + packet + ":\n" + ByteBufUtil.prettyHexDump(packet.getBuffer()));

                // Try to figure out what packet got sent.
                if (packet.isHasSplit()) {
                    Optional<ByteBuf> possiblyReassembled = session.addSplitPacket(packet);
                    if (possiblyReassembled.isPresent()) {
                        ByteBuf reassembled = possiblyReassembled.get();
                        try {
                            RakNetPackage pkg = bruteForceDecode(reassembled);
                            handlePackage(pkg, session);
                        } finally {
                            reassembled.release();
                        }
                    }
                } else {
                    // Try to decode the full packet.
                    RakNetPackage pkg = bruteForceDecode(packet.getBuffer());
                    handlePackage(pkg, session);
                }
            }
        }
    }

    private RakNetPackage bruteForceDecode(ByteBuf buf) throws Exception {
        RakNetPackage pkg;

        for (PacketType type : PacketType.values()) {
            ByteBuf slice = buf.slice();
            try {
                pkg = PacketRegistry.tryDecode(slice, type);
            } catch (Exception e) {
                continue;
            }

            if (pkg == null)
                continue;

            if (slice.isReadable()) {
                // Not all data was read?
                LOGGER.error("When using " + type + ", bytes were left: " + ByteBufUtil.hexDump(slice));
            } else {
                return pkg;
            }
        }

        ByteBuf smallSlice = buf.slice(0, Math.min(buf.readableBytes(), 16));
        throw new Exception("Unable to create packet for ID " + Integer.toHexString(smallSlice.getUnsignedByte(0)) + " (first 16 bytes: " + ByteBufUtil.hexDump(smallSlice) + ").");
    }

    private void handlePackage(RakNetPackage netPackage, UserSession session) throws Exception {
        System.out.println("[Package] " + netPackage);

        if (session.getHandler() == null) {
            LOGGER.error("Session " + session.getRemoteAddress() + " has no handler!?!?!");
            return;
        }

        // Special cases we need to handle here.

        // McpeBatch: Multiple packets. This method will handle everything.
        if (netPackage instanceof McpeBatch) {
            for (RakNetPackage aPackage : ((McpeBatch) netPackage).getPackages()) {
                handlePackage(aPackage, session);
            }
            return;
        }
        // Connected Ping
        if (netPackage instanceof ConnectedPingPacket) {
            ConnectedPingPacket request = (ConnectedPingPacket) netPackage;
            ConnectedPongPacket response = new ConnectedPongPacket();
            response.setPingTime(request.getPingTime());
            response.setPongTime(System.currentTimeMillis());
            session.sendUrgentPackage(response);
            return;
        }
        // Connection Request
        if (netPackage instanceof ConnectionRequestPacket) {
            ConnectionRequestPacket request = (ConnectionRequestPacket) netPackage;
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
        // Disconnection
        if (netPackage instanceof DisconnectNotificationPacket) {
            session.close();
            return;
        }

        // Dispatch block...
        if (netPackage instanceof McpeLogin) {
            session.getHandler().handle((McpeLogin) netPackage);
        }
    }
}
