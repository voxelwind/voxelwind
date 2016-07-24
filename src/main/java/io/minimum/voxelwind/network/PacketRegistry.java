package io.minimum.voxelwind.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import io.minimum.voxelwind.network.mcpe.packets.McpeLogin;
import io.minimum.voxelwind.network.mcpe.packets.McpeServerHandshake;
import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.packets.*;
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
                            .build())
                    .put(PacketType.MCPE, ImmutableBiMap.<Integer, Class<? extends RakNetPackage>>builder()
                            .put(0x01, McpeLogin.class)
                            .put(0x03, McpeServerHandshake.class)
                            .build())
                    .build();

    private PacketRegistry() {

    }

    public static RakNetPackage tryDecode(ByteBuf buf, PacketType type) {
        int id = buf.readByte();
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

    public static ByteBuf tryEncode(RakNetPackage pkg, PacketType type) {
        Integer id = PACKAGE_BY_ID.get(type).inverse().get(pkg.getClass());
        if (id == null) {
            throw new RuntimeException("Package does not exist");
        }

        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        buf.writeByte((id & 0xFF));
        pkg.encode(buf);

        return buf;
    }
}
