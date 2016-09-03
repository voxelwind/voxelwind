package com.voxelwind.server.network;

import com.voxelwind.server.network.mcpe.annotations.BatchDisallowed;
import com.voxelwind.server.network.mcpe.packets.*;
import com.voxelwind.server.network.raknet.packets.*;
import gnu.trove.TCollections;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.Arrays;

public class PacketRegistry {
    private static final Class<? extends NetworkPackage>[] RAKNET_PACKETS = new Class[256];
    private static final Class<? extends NetworkPackage>[] MCPE_PACKETS = new Class[256];
    private static final TObjectIntMap<Class<? extends NetworkPackage>> PACKAGE_MAPPING;

    static {
        RAKNET_PACKETS[0x00] = ConnectedPingPacket.class;
        RAKNET_PACKETS[0x01] = UnconnectedPingPacket.class;
        RAKNET_PACKETS[0x03] = ConnectedPongPacket.class;
        RAKNET_PACKETS[0x05] = OpenConnectionRequest1Packet.class;
        RAKNET_PACKETS[0x06] = OpenConnectionResponse1Packet.class;
        RAKNET_PACKETS[0x07] = OpenConnectionRequest2Packet.class;
        RAKNET_PACKETS[0x08] = OpenConnectionResponse2Packet.class;
        RAKNET_PACKETS[0x09] = ConnectionRequestPacket.class;
        RAKNET_PACKETS[0x10] = ConnectionResponsePacket.class;
        RAKNET_PACKETS[0x13] = NewIncomingConnectionPacket.class;
        RAKNET_PACKETS[0x14] = NoFreeIncomingConnectionsPacket.class;
        RAKNET_PACKETS[0x15] = DisconnectNotificationPacket.class;
        RAKNET_PACKETS[0x1c] = UnconnectedPongPacket.class;
        RAKNET_PACKETS[0xa0] = NakPacket.class;
        RAKNET_PACKETS[0xc0] = AckPacket.class;
        RAKNET_PACKETS[0xfe] = McpeWrapper.class; // Technically not an MCPE packet, but here for convenience

        MCPE_PACKETS[0x01] = McpeLogin.class;
        MCPE_PACKETS[0x02] = McpePlayStatus.class;
        MCPE_PACKETS[0x03] = McpeServerHandshake.class;
        MCPE_PACKETS[0x04] = McpeClientMagic.class;
        MCPE_PACKETS[0x05] = McpeDisconnect.class;
        MCPE_PACKETS[0x06] = McpeBatch.class;
        MCPE_PACKETS[0x07] = McpeText.class;
        MCPE_PACKETS[0x08] = McpeSetTime.class;
        MCPE_PACKETS[0x09] = McpeStartGame.class;
        MCPE_PACKETS[0x0a] = McpeAddPlayer.class;
        MCPE_PACKETS[0x0b] = McpeAddEntity.class;
        MCPE_PACKETS[0x0c] = McpeRemoveEntity.class;
        MCPE_PACKETS[0x0f] = McpeMoveEntity.class;
        MCPE_PACKETS[0x10] = McpeMovePlayer.class;
        MCPE_PACKETS[0x13] = McpeUpdateBlock.class;
        MCPE_PACKETS[0x18] = McpeEntityEvent.class;
        MCPE_PACKETS[0x1a] = McpeUpdateAttributes.class;
        MCPE_PACKETS[0x1b] = McpeMobEquipment.class;
        MCPE_PACKETS[0x20] = McpePlayerAction.class;
        MCPE_PACKETS[0x22] = McpeSetEntityData.class;
        MCPE_PACKETS[0x23] = McpeSetEntityMotion.class;
        MCPE_PACKETS[0x26] = McpeSetSpawnPosition.class;
        MCPE_PACKETS[0x27] = McpeAnimate.class;
        MCPE_PACKETS[0x28] = McpeRespawn.class;
        MCPE_PACKETS[0x2a] = McpeContainerOpen.class;
        MCPE_PACKETS[0x2b] = McpeContainerClose.class;
        MCPE_PACKETS[0x2c] = McpeContainerSetSlot.class;
        MCPE_PACKETS[0x2d] = McpeContainerSetData.class;
        MCPE_PACKETS[0x2e] = McpeContainerSetContents.class;
        MCPE_PACKETS[0x31] = McpeAdventureSettings.class;
        MCPE_PACKETS[0x34] = McpeFullChunkData.class;
        MCPE_PACKETS[0x37] = McpeSetPlayerGameMode.class;
        MCPE_PACKETS[0x3d] = McpeRequestChunkRadius.class;
        MCPE_PACKETS[0x3e] = McpeChunkRadiusUpdated.class;

        TObjectIntMap<Class<? extends NetworkPackage>> classToIdMap = new TObjectIntHashMap<>(64, 0.75f, -1);
        for (int i = 0; i < RAKNET_PACKETS.length; i++) {
            Class clazz = RAKNET_PACKETS[i];
            if (clazz != null) {
                classToIdMap.put(clazz, i);
            }
        }

        for (int i = 0; i < MCPE_PACKETS.length; i++) {
            Class clazz = MCPE_PACKETS[i];
            if (clazz != null) {
                classToIdMap.put(clazz, i);
            }
        }

        PACKAGE_MAPPING = TCollections.unmodifiableMap(classToIdMap);
    }

    private PacketRegistry() {

    }

    public static NetworkPackage tryDecode(ByteBuf buf, PacketType type) {
        return tryDecode(buf, type, false);
    }

    public static NetworkPackage tryDecode(ByteBuf buf, PacketType type, boolean fromBatch) {
        int id = buf.readUnsignedByte();
        Class<? extends NetworkPackage> pkgClass;
        switch (type) {
            case RAKNET:
                pkgClass = RAKNET_PACKETS[id];
                break;
            case MCPE:
                pkgClass = MCPE_PACKETS[id];
                break;
            default:
                throw new IllegalArgumentException("Invalid PacketType");
        }

        if (pkgClass == null) {
            return null;
        }

        if (fromBatch) {
            if (pkgClass.isAnnotationPresent(BatchDisallowed.class)) {
                return null;
            }
        }

        NetworkPackage netPackage;
        try {
            netPackage = pkgClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to create packet instance", e);
        }

        netPackage.decode(buf);
        return netPackage;
    }

    public static int getId(NetworkPackage pkg) {
        Class<? extends NetworkPackage> pkgClass = pkg.getClass();
        int res = PACKAGE_MAPPING.get(pkgClass);
        if (res == -1) {
            throw new IllegalArgumentException("Packet ID for " + pkgClass.getName() + " does not exist.");
        }
        return res;
    }

    public static ByteBuf tryEncode(NetworkPackage pkg) {
        int id = getId(pkg);

        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer();
        buf.writeByte((id & 0xFF));
        pkg.encode(buf);

        return buf;
    }
}
