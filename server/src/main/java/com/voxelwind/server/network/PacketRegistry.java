package com.voxelwind.server.network;

import com.voxelwind.server.network.mcpe.annotations.BatchDisallowed;
import com.voxelwind.server.network.mcpe.packets.*;
import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.raknet.packets.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.Arrays;

public class PacketRegistry {
    private static final Class<? extends RakNetPackage>[] RAKNET_PACKETS = new Class[256];
    private static final Class<? extends RakNetPackage>[] MCPE_PACKETS = new Class[256];

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
        MCPE_PACKETS[0x10] = McpeMoveEntity.class;
        MCPE_PACKETS[0x11] = McpeMovePlayer.class;
        MCPE_PACKETS[0x14] = McpeUpdateBlock.class;
        MCPE_PACKETS[0x19] = McpeEntityEvent.class;
        MCPE_PACKETS[0x1b] = McpeUpdateAttributes.class;
        MCPE_PACKETS[0x1c] = McpeMobEquipment.class;
        MCPE_PACKETS[0x20] = McpePlayerAction.class;
        MCPE_PACKETS[0x22] = McpeSetEntityData.class;
        MCPE_PACKETS[0x23] = McpeSetEntityMotion.class;
        MCPE_PACKETS[0x26] = McpeSetSpawnPosition.class;
        MCPE_PACKETS[0x27] = McpeAnimate.class;
        MCPE_PACKETS[0x28] = McpeRespawn.class;
        MCPE_PACKETS[0x2b] = McpeContainerOpen.class;
        MCPE_PACKETS[0x2c] = McpeContainerClose.class;
        MCPE_PACKETS[0x2d] = McpeContainerSetSlot.class;
        MCPE_PACKETS[0x2e] = McpeContainerSetData.class;
        MCPE_PACKETS[0x2f] = McpeContainerSetContents.class;
        MCPE_PACKETS[0x32] = McpeAdventureSettings.class;
        MCPE_PACKETS[0x35] = McpeFullChunkData.class;
        MCPE_PACKETS[0x39] = McpeSetPlayerGameMode.class;
        MCPE_PACKETS[0x3f] = McpeRequestChunkRadius.class;
        MCPE_PACKETS[0x40] = McpeChunkRadiusUpdated.class;
    }

    private PacketRegistry() {

    }

    public static RakNetPackage tryDecode(ByteBuf buf, PacketType type) {
        return tryDecode(buf, type, false);
    }

    public static RakNetPackage tryDecode(ByteBuf buf, PacketType type, boolean fromBatch) {
        int id = buf.readUnsignedByte();
        Class<? extends RakNetPackage> pkgClass;
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
        // TODO: Might be a regression going from two O(1) operations to two O(n) operations. Requires some profiling.
        Class<? extends RakNetPackage> pkgClass = pkg.getClass();
        int res = Arrays.asList(RAKNET_PACKETS).indexOf(pkg.getClass());
        if (res == -1) {
            res = Arrays.asList(MCPE_PACKETS).indexOf(pkg.getClass());
        }
        if (res == -1) {
            throw new IllegalArgumentException("Packet ID for " + pkgClass.getName() + " does not exist.");
        }
        return res;
    }

    public static ByteBuf tryEncode(RakNetPackage pkg) {
        int id = getId(pkg);

        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer();
        buf.writeByte((id & 0xFF));
        pkg.encode(buf);

        return buf;
    }
}
