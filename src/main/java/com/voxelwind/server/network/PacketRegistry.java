package com.voxelwind.server.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.voxelwind.server.network.mcpe.annotations.BatchDisallowed;
import com.voxelwind.server.network.mcpe.packets.*;
import com.voxelwind.server.network.raknet.packets.*;
import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.Map;

public class PacketRegistry {
    private static final Map<PacketType, BiMap<Integer, Class<? extends RakNetPackage>>> PACKAGE_BY_ID =
            ImmutableMap.<PacketType, BiMap<Integer, Class<? extends RakNetPackage>>>builder()
                    .put(PacketType.RAKNET, ImmutableBiMap.<Integer, Class<? extends RakNetPackage>>builder()
                            .put(0x00, ConnectedPingPacket.class)
                            .put(0x01, UnconnectedPingPacket.class)
                            .put(0x03, ConnectedPongPacket.class)
                            .put(0x05, OpenConnectionRequest1Packet.class)
                            .put(0x06, OpenConnectionResponse1Packet.class)
                            .put(0x07, OpenConnectionRequest2Packet.class)
                            .put(0x08, OpenConnectionResponse2Packet.class)
                            .put(0x09, ConnectionRequestPacket.class)
                            .put(0x10, ConnectionResponsePacket.class)
                            .put(0x13, NewIncomingConnectionPacket.class)
                            .put(0x15, DisconnectNotificationPacket.class)
                            .put(0x1c, UnconnectedPongPacket.class)
                            .put(0xa0, NakPacket.class)
                            .put(0xc0, AckPacket.class)
                            .put(0xfe, McpeWrapper.class) // Technically not an MCPE packet, but here for convenience
                            .build())
                    .put(PacketType.MCPE, ImmutableBiMap.<Integer, Class<? extends RakNetPackage>>builder()
                            .put(0x01, McpeLogin.class)
                            .put(0x02, McpePlayStatus.class)
                            .put(0x03, McpeServerHandshake.class)
                            .put(0x04, McpeClientMagic.class)
                            .put(0x05, McpeDisconnect.class)
                            .put(0x06, McpeBatch.class)
                            .put(0x09, McpeStartGame.class)
                            .put(0x20, McpePlayerAction.class)
                            .put(0x23, McpeSetEntityMotion.class)
                            .put(0x27, McpeAnimate.class)
                            .put(0x28, McpeRespawn.class)
                            .put(0x31, McpeAdventureSettings.class)
                            .put(0x34, McpeFullChunkData.class)
                            .put(0x3d, McpeRequestChunkRadius.class)
                            .put(0x3e, McpeChunkRadiusUpdated.class)
                            .build())
                    .build();

    private PacketRegistry() {

    }

    public static RakNetPackage tryDecode(ByteBuf buf, PacketType type) {
        int id = buf.readUnsignedByte();
        Class<? extends RakNetPackage> pkgClass = PACKAGE_BY_ID.get(type).get(id);
        if (pkgClass == null)
            return null;

        RakNetPackage netPackage;
        try {
            netPackage = pkgClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to create packet instance", e);
        }

        netPackage.decode(buf);
        return netPackage;
    }

    public static RakNetPackage tryDecode(ByteBuf buf, PacketType type, boolean fromBatch) {
        int id = buf.readUnsignedByte();
        Class<? extends RakNetPackage> pkgClass = PACKAGE_BY_ID.get(type).get(id);
        if (pkgClass == null)
            return null;

        if (fromBatch) {
            if (pkgClass.isAnnotationPresent(BatchDisallowed.class)) {
                return null;
            }
        }

        RakNetPackage netPackage;
        try {
            netPackage = pkgClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to create packet instance", e);
        }

        netPackage.decode(buf);
        return netPackage;
    }

    public static Integer getId(RakNetPackage pkg) {
        for (Map.Entry<PacketType, BiMap<Integer, Class<? extends RakNetPackage>>> entry : PACKAGE_BY_ID.entrySet()) {
            Integer res = entry.getValue().inverse().get(pkg.getClass());
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    public static Integer getId(RakNetPackage pkg, PacketType type) {
        return PACKAGE_BY_ID.get(type).inverse().get(pkg.getClass());
    }

    public static ByteBuf tryEncode(RakNetPackage pkg) {
        Integer id = getId(pkg);
        if (id == null) {
            throw new IllegalArgumentException("Package " + pkg.getClass() + " is not registered");
        }

        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        buf.writeByte((id & 0xFF));
        pkg.encode(buf);

        return buf;
    }
}
