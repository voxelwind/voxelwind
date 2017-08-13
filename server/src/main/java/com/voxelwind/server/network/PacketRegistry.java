package com.voxelwind.server.network;

import com.voxelwind.server.network.mcpe.annotations.BatchDisallowed;
import com.voxelwind.server.network.mcpe.packets.*;
import com.voxelwind.server.network.raknet.packets.*;
import gnu.trove.TCollections;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

@SuppressWarnings("unchecked")
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
        MCPE_PACKETS[0x03] = McpeServerToClientHandshake.class;
        MCPE_PACKETS[0x04] = McpeClientToServerHandshake.class;
        MCPE_PACKETS[0x05] = McpeDisconnect.class;
        MCPE_PACKETS[0x06] = McpeResourcePacksInfo.class;
        //MCPE_PACKETS[0x07] = McpeResourcePackStack.class;
        MCPE_PACKETS[0x08] = McpeResourcePackClientResponse.class;
        MCPE_PACKETS[0x09] = McpeText.class;
        MCPE_PACKETS[0x0a] = McpeSetTime.class;
        MCPE_PACKETS[0x0b] = McpeStartGame.class;
        MCPE_PACKETS[0x0c] = McpeAddPlayer.class;
        MCPE_PACKETS[0x0d] = McpeAddEntity.class;
        MCPE_PACKETS[0x0e] = McpeRemoveEntity.class;
        MCPE_PACKETS[0x0f] = McpeAddItemEntity.class;
        //MCPE_PACKETS[0x10] = McpeAddHangingEntity.class;
        MCPE_PACKETS[0x11] = McpeTakeItemEntity.class;
        MCPE_PACKETS[0x12] = McpeMoveEntity.class;
        MCPE_PACKETS[0x13] = McpeMovePlayer.class;
        //MCPE_PACKETS[0x14] = McpeRiderJump.class;
        MCPE_PACKETS[0x15] = McpeRemoveBlock.class;
        MCPE_PACKETS[0x16] = McpeUpdateBlock.class;
        //MCPE_PACKETS[0x17] = McpeAddPainting.class;
        //MCPE_PACKETS[0x18] = McpeExplode.class;
        //MCPE_PACKETS[0x19] = McpeLevelSoundEvent.class;
        //MCPE_PACKETS[0x1a] = McpeLevelEvent.class;
        //MCPE_PACKETS[0x1b] = McpeBlockEvent.class;
        MCPE_PACKETS[0x1c] = McpeEntityEvent.class;
        //MCPE_PACKETS[0x1d] = McpeMobEffect.class;
        MCPE_PACKETS[0x1e] = McpeUpdateAttributes.class;
        MCPE_PACKETS[0x1f] = McpeMobEquipment.class;
        //MCPE_PACKETS[0x20] = McpeMobArmorEquipment.class;
        MCPE_PACKETS[0x21] = McpeInteract.class;
        //MCPE_PACKETS[0x22] = McpeBlockPickRequest.class;
        MCPE_PACKETS[0x23] = McpeUseItem.class;
        MCPE_PACKETS[0x24] = McpePlayerAction.class;
        //MCPE_PACKETS[0x25] = McpeEntityFall.class;
        //MCPE_PACKETS[0x26] = McpeHurtArmor.class
        MCPE_PACKETS[0x27] = McpeSetEntityData.class;
        MCPE_PACKETS[0x28] = McpeSetEntityMotion.class;
        //MCPE_PACKETS[0x29] = McpeSetEntityLink.class;
        MCPE_PACKETS[0x2a] = McpeSetHealth.class;
        MCPE_PACKETS[0x2b] = McpeSetSpawnPosition.class;
        MCPE_PACKETS[0x2c] = McpeAnimate.class;
        MCPE_PACKETS[0x2d] = McpeRespawn.class;
        MCPE_PACKETS[0x2e] = McpeDropItem.class;
        //MCPE_PACKETS[0x2f] = McpeInventoryAction.class;
        MCPE_PACKETS[0x30] = McpeContainerOpen.class;
        MCPE_PACKETS[0x31] = McpeContainerClose.class;
        MCPE_PACKETS[0x32] = McpeContainerSetSlot.class;
        MCPE_PACKETS[0x33] = McpeContainerSetData.class;
        MCPE_PACKETS[0x34] = McpeContainerSetContent.class;
        //MCPE_PACKETS[0x35] = McpeCraftingData.class
        //MCPE_PACKETS[0x36] = McpeCraftingEvent.class
        MCPE_PACKETS[0x37] = McpeAdventureSettings.class;
        MCPE_PACKETS[0x38] = McpeBlockEntityData.class;
        //MCPE_PACKETS[0x39] = McpePlayerInput.class;
        MCPE_PACKETS[0x3a] = McpeFullChunkData.class;
        MCPE_PACKETS[0x3b] = McpeSetCommandsEnabled.class;
        //MCPE_PACKETS[0x3c] = McpeSetDifficulty.class;
        MCPE_PACKETS[0x3d] = McpeChangeDimension.class;
        MCPE_PACKETS[0x3e] = McpeSetPlayerGameType.class;
        MCPE_PACKETS[0x3f] = McpePlayerList.class;
        //MCPE_PACKETS[0x40] = McpeSimpleEvent.class;
        //MCPE_PACKETS[0x41] = McpeEvent.class;
        //MCPE_PACKETS[0x42] = McpeSpawnExperienceOrb.class;
        //MCPE_PACKETS[0x43] = McpeClientboundMapItemData.class;
        //MCPE_PACKETS[0x44] = McpeMapInfoRequest.class;
        MCPE_PACKETS[0x45] = McpeRequestChunkRadius.class;
        MCPE_PACKETS[0x46] = McpeChunkRadiusUpdate.class;
        //MCPE_PACKETS[0x47] = McpeItemFramDropItem.class;
        //MCPE_PACKETS[0x48] = McpeReplaceSelectedItem.class;
        //MCPE_PACKETS[0x49] = McpeGameRulesChanged.class;
        //MCPE_PACKETS[0x4a] = McpeCamera.class;
        //MCPE_PACKETS[0x4b] = McpeAddItem.class;
        //MCPE_PACKETS[0x4c] = McpeBossEvent.class;
        //MCPE_PACKETS[0x4d] = McpeShowCredits.class;
        MCPE_PACKETS[0x4e] = McpeAvailableCommands.class;
        MCPE_PACKETS[0x4f] = McpeCommandStep.class;
        //MCPE_PACKETS[0x50] = McpeCommandBlockUpdate.class;
        //MCPE_PACKETS[0x51] = McpeUpdateTrade.class;
        //MCPE_PACKETS[0x52] = McpeUpdateEquip.class;
        //MCPE_PACKETS[0x53] = McpeResourcePackDataInfo.class;
        //MCPE_PACKETS[0x54] = McpeResourcePackChunkData.class;
        //MCPE_PACKETS[0x55] = McpeResourcePackChunkRequest.class;
        //MCPE_PACKETS[0x56] = McpeTransfer.class;
        //MCPE_PACKETS[0x57] = McpePlaySound.class;
        //MCPE_PACKETS[0x58] = MspeStopSound.class;
        //MCPE_PACKETS[0x59] = McpeSetTitle.class;
        //MCPE_PACKETS[0x5a] = McpeAddBehaviorTreePacket.class;
        //MCPE_PACKETS[0x5b] = McpeStructureBlockUpdatePacket.class;
        //MCPE_PACKETS[0x5c] = McpeShowStoreOffer.class;
        //MCPE_PACKETS[0x5d] = McpePurchaseReceipt.class;

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
