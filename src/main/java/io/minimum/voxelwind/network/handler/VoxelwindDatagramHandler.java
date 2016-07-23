package io.minimum.voxelwind.network.handler;

import io.minimum.voxelwind.VoxelwindServer;
import io.minimum.voxelwind.network.PacketRegistry;
import io.minimum.voxelwind.network.PacketType;
import io.minimum.voxelwind.network.mcpe.packets.McpeBatch;
import io.minimum.voxelwind.network.mcpe.packets.McpeLogin;
import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.datagrams.EncapsulatedRakNetPacket;
import io.minimum.voxelwind.network.raknet.datagrams.RakNetDatagramFlags;
import io.minimum.voxelwind.network.raknet.enveloped.AddressedRakNetDatagram;
import io.minimum.voxelwind.network.raknet.packets.AckPacket;
import io.minimum.voxelwind.network.session.UserSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class VoxelwindDatagramHandler extends SimpleChannelInboundHandler<AddressedRakNetDatagram> {
    private final VoxelwindServer server;
    private static final Logger LOGGER = LogManager.getLogger(VoxelwindDatagramHandler.class);

    public VoxelwindDatagramHandler(VoxelwindServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AddressedRakNetDatagram datagram) throws Exception {
        UserSession session = server.getSessionManager().get(datagram.sender());

        if (session == null)
            return;

        // Acknowledge receipt of the datagram.
        session.enqueueAck(datagram.content().getDatagramSequenceNumber());

        // Check the datagram contents.
        if (datagram.content().getFlags().isValid()) {
            for (EncapsulatedRakNetPacket packet : datagram.content().getPackets()) {
                if (packet.isHasSplit()) {
                    Optional<ByteBuf> possiblyReassembled = session.addSplitPacket(packet);
                    if (possiblyReassembled.isPresent()) {
                        ByteBuf reassembled = possiblyReassembled.get();
                        try {
                            RakNetPackage pkg = PacketRegistry.tryDecode(reassembled, PacketType.MCPE);
                            if (pkg != null) {
                                handlePackage(pkg, session);
                            }
                        } finally {
                            reassembled.release();
                        }
                    }
                } else {
                    // Try to decode the full packet.
                    RakNetPackage pkg = PacketRegistry.tryDecode(packet.getBuffer(), PacketType.MCPE);
                    if (pkg != null) {
                        handlePackage(pkg, session);
                    }
                }
            }
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

        if (session.getHandler() == null) {
            LOGGER.error("Session " + session.getRemoteAddress() + " has no handler!?!?!");
            return;
        }

        if (netPackage instanceof McpeLogin) {
            session.getHandler().handle((McpeLogin) netPackage);
        }
    }
}
