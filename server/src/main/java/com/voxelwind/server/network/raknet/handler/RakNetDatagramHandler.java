package com.voxelwind.server.network.raknet.handler;

import com.google.common.net.InetAddresses;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.PacketRegistry;
import com.voxelwind.server.network.PacketType;
import com.voxelwind.server.network.mcpe.packets.*;
import com.voxelwind.server.network.raknet.RakNetSession;
import com.voxelwind.server.network.raknet.datagrams.EncapsulatedRakNetPacket;
import com.voxelwind.server.network.raknet.datastructs.IntRange;
import com.voxelwind.server.network.raknet.enveloped.AddressedRakNetDatagram;
import com.voxelwind.server.network.raknet.enveloped.DirectAddressedRakNetPacket;
import com.voxelwind.server.network.raknet.packets.*;
import com.voxelwind.server.network.session.McpeSession;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Optional;

public class RakNetDatagramHandler extends SimpleChannelInboundHandler<AddressedRakNetDatagram> {
    private static final Logger LOGGER = LogManager.getLogger(RakNetDatagramHandler.class);
    private final VoxelwindServer server;

    public RakNetDatagramHandler(VoxelwindServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AddressedRakNetDatagram datagram) throws Exception {
        McpeSession session = server.getSessionManager().get(datagram.sender());

        if (session == null)
            return;

        // Make sure a RakNet session is backing this packet.
        if (!(session.getConnection() instanceof RakNetSession)) {
            return;
        }

        RakNetSession rakNetSession = (RakNetSession) session.getConnection();

        // Acknowledge receipt of the datagram.
        AckPacket ackPacket = new AckPacket();
        ackPacket.getIds().add(new IntRange(datagram.content().getDatagramSequenceNumber()));
        ctx.writeAndFlush(new DirectAddressedRakNetPacket(ackPacket, datagram.sender()), ctx.voidPromise());

        // Update session touch time.
        session.touch();

        // Check the datagram contents.
        if (datagram.content().getFlags().isValid()) {
            for (EncapsulatedRakNetPacket packet : datagram.content().getPackets()) {
                // Try to figure out what packet got sent.
                if (packet.isHasSplit()) {
                    Optional<ByteBuf> possiblyReassembled = rakNetSession.addSplitPacket(packet);
                    if (possiblyReassembled.isPresent()) {
                        ByteBuf reassembled = possiblyReassembled.get();
                        try {
                            NetworkPackage pkg = PacketRegistry.tryDecode(reassembled, PacketType.RAKNET);
                            handlePackage(pkg, session);
                        } finally {
                            reassembled.release();
                        }
                    }
                } else {
                    // Try to decode the full packet.
                    NetworkPackage pkg = PacketRegistry.tryDecode(packet.getBuffer(), PacketType.RAKNET);
                    handlePackage(pkg, session);
                }
            }
        }
    }

    private void handlePackage(NetworkPackage netPackage, McpeSession session) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Inbound package: {}", netPackage);
        }

        if (netPackage == null) {
            return;
        }

        if (session.getHandler() == null) {
            LOGGER.error("Session " + session.getRemoteAddress() + " has no handler!?!?!");
            return;
        }

        // Special cases we need to handle here.
        // McpeWrapper: Encrypted packet.
        if (netPackage instanceof McpeWrapper) {
            ByteBuf cleartext = null;
            try {
                if (session.isEncrypted()) {
                    cleartext = PooledByteBufAllocator.DEFAULT.directBuffer();
                    session.getDecryptionCipher().cipher(((McpeWrapper) netPackage).getWrapped(), cleartext);
                    LOGGER.debug("[CHECKSUM] {}", ByteBufUtil.hexDump(cleartext.slice(cleartext.readableBytes() - 8, 8)));
                    cleartext = cleartext.slice(0, cleartext.readableBytes() - 8);
                } else {
                    cleartext = ((McpeWrapper) netPackage).getWrapped();
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("[HEX IN] {}", ByteBufUtil.prettyHexDump(cleartext));
                }

                NetworkPackage pkg = PacketRegistry.tryDecode(cleartext, PacketType.MCPE);
                handlePackage(pkg, session);
            } finally {
                if (cleartext != null && cleartext != ((McpeWrapper) netPackage).getWrapped()) {
                    cleartext.release();
                }
            }
            return;
        }

        // Connected Ping
        if (netPackage instanceof ConnectedPingPacket) {
            ConnectedPingPacket request = (ConnectedPingPacket) netPackage;
            ConnectedPongPacket response = new ConnectedPongPacket();
            response.setPingTime(request.getPingTime());
            response.setPongTime(System.currentTimeMillis());
            session.sendImmediatePackage(response);
            return;
        }
        // Connection Request
        if (netPackage instanceof ConnectionRequestPacket) {
            ConnectionRequestPacket request = (ConnectionRequestPacket) netPackage;
            ConnectionResponsePacket response = new ConnectionResponsePacket();
            response.setIncomingTimestamp(request.getTimestamp());
            response.setSystemTimestamp(System.currentTimeMillis());
            response.setSystemAddress(session.getRemoteAddress().orElse(new InetSocketAddress(InetAddress.getLoopbackAddress(), 19132)));
            InetSocketAddress[] addresses = new InetSocketAddress[10];
            Arrays.fill(addresses, new InetSocketAddress(InetAddresses.forString("255.255.255.255"), 19132));
            addresses[0] = new InetSocketAddress(InetAddress.getLoopbackAddress(), 19132);
            response.setSystemAddresses(addresses);
            response.setSystemIndex((short) 0);
            session.sendImmediatePackage(response);
            return;
        }
        // Disconnection
        if (netPackage instanceof DisconnectNotificationPacket) {
            session.close();
            return;
        }

        // Unknown
        if (netPackage instanceof McpeUnknown) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unknown packet received with ID " + Integer.toHexString(((McpeUnknown) netPackage).getId()));
                LOGGER.debug("Dump: {}", ByteBufUtil.hexDump(((McpeUnknown) netPackage).getBuf()));
            }
            ((McpeUnknown) netPackage).getBuf().release();
        }

        // McpeBatch: Multiple packets. This method will handle everything.
        if (netPackage instanceof McpeBatch) {
            for (NetworkPackage aPackage : ((McpeBatch) netPackage).getPackages()) {
                handlePackage(aPackage, session);
            }
            return;
        }

        // Dispatch block...
        if (netPackage instanceof McpeLogin) {
            session.getHandler().handle((McpeLogin) netPackage);
        }
        if (netPackage instanceof McpeClientMagic) {
            session.getHandler().handle((McpeClientMagic) netPackage);
        }
        if (netPackage instanceof McpeRequestChunkRadius) {
            session.getHandler().handle((McpeRequestChunkRadius) netPackage);
        }
        if (netPackage instanceof McpePlayerAction) {
            session.getHandler().handle((McpePlayerAction) netPackage);
        }
        if (netPackage instanceof McpeAnimate) {
            session.getHandler().handle((McpeAnimate) netPackage);
        }
        if (netPackage instanceof McpeText) {
            session.getHandler().handle((McpeText) netPackage);
        }
        if (netPackage instanceof McpeMovePlayer) {
            session.getHandler().handle((McpeMovePlayer) netPackage);
        }
        if (netPackage instanceof McpeContainerClose) {
            session.getHandler().handle((McpeContainerClose) netPackage);
        }
        if (netPackage instanceof McpeContainerSetSlot) {
            session.getHandler().handle((McpeContainerSetSlot) netPackage);
        }
        if (netPackage instanceof McpeMobEquipment) {
            session.getHandler().handle((McpeMobEquipment) netPackage);
        }
        if (netPackage instanceof McpeRemoveBlock) {
            session.getHandler().handle((McpeRemoveBlock) netPackage);
        }
        if (netPackage instanceof McpeUseItem) {
            session.getHandler().handle((McpeUseItem) netPackage);
        }
        if (netPackage instanceof McpeDropItem) {
            session.getHandler().handle((McpeDropItem) netPackage);
        }
        if (netPackage instanceof McpeResourcePackClientResponse) {
            session.getHandler().handle((McpeResourcePackClientResponse) netPackage);
        }
        if (netPackage instanceof McpeCommandStep) {
            session.getHandler().handle((McpeCommandStep) netPackage);
        }
    }
}
