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
        MCPE_PACKETS[0x03] = McpeServerHandshake.class;  // McpeServerExchange.class
        MCPE_PACKETS[0x04] = McpeClientMagic.class;
        MCPE_PACKETS[0x05] = McpeDisconnect.class;
        MCPE_PACKETS[0x06] = McpeBatch.class;
        MCPE_PACKETS[0x07] = McpeResourcePackInfo.class;
	    // RESOURCE_PACK_STACK_PACKET = 0x08; //McpeResourcePackStack.class
	    MCPE_PACKETS[0x09] = McpeResourcePackClientResponse.class;
        MCPE_PACKETS[0x0a] = McpeText.class;
        MCPE_PACKETS[0x0b] = McpeSetTime.class;
        MCPE_PACKETS[0x0c] = McpeStartGame.class;
        MCPE_PACKETS[0x0d] = McpeAddPlayer.class;
        MCPE_PACKETS[0x0e] = McpeAddEntity.class;
        MCPE_PACKETS[0x0f] = McpeRemoveEntity.class;
        MCPE_PACKETS[0x10] = McpeAddItemEntity.class;
        // ADD_HANGING_ENTITY_PACKET = 0x11; McpeAddHangingEntity.class
        MCPE_PACKETS[0x12] = McpeTakeItem.class;  //McpeTakeItemEntity.class
        MCPE_PACKETS[0x13] = McpeMoveEntity.class;
        MCPE_PACKETS[0x14] = McpeMovePlayer.class;
        // RIDER_JUMP_PACKET = 0x15;  //McpeRiderJump.class
        MCPE_PACKETS[0x16] = McpeRemoveBlock.class;
        MCPE_PACKETS[0x17] = McpeUpdateBlock.class;
        // ADD_PAINTING_PACKET = 0x18;  // McpeAddPainting.class
        // EXPLODE_PACKET = 0x19;  // McpeExplode.class
        // LEVEL_SOUND_EVENT_PACKET = 0x1a; // McpeLevelSoundEvent.class
        // LEVEL_EVENT_PACKET = 0x1b;  // McpeLevelEvent.class
        // BLOCK_EVENT_PACKET = 0x1c;  // McpeBlockEvent.class
        MCPE_PACKETS[0x1d] = McpeEntityEvent.class;
        // MOB_EFFECT_PACKET = 0x1e; // McpeMobEffect.class
        MCPE_PACKETS[0x1f] = McpeUpdateAttributes.class;
        MCPE_PACKETS[0x20] = McpeMobEquipment.class;
        // MOB_ARMOR_EQUIPMENT_PACKET = 0x21; // McpeMobArmorEquipment.class
        MCPE_PACKETS[0x22] = McpeInteract.class;
        // BLOCK_PICK_REQUEST_PACKET = 0x23; // McpeBlockPickRequest.class
        MCPE_PACKETS[0x24] = McpeUseItem.class;
        MCPE_PACKETS[0x25] = McpePlayerAction.class;
        // PLAYER_FALL_PACKET = 0x26; // McpePlayerFall.class
        // HURT_ARMOR_PACKET = 0x27; // McpeHurtArmor.class
        MCPE_PACKETS[0x28] = McpeSetEntityData.class;
        MCPE_PACKETS[0x29] = McpeSetEntityMotion.class;
        // SET_ENTITY_LINK_PACKET = 0x2a;   // McpeSetEntityLink.class
        MCPE_PACKETS[0x2b] = McpeSetHealth.class;
        MCPE_PACKETS[0x2c] = McpeSetSpawnPosition.class;
        MCPE_PACKETS[0x2d] = McpeAnimate.class;
        MCPE_PACKETS[0x2e] = McpeRespawn.class;
        MCPE_PACKETS[0x2f] = McpeDropItem.class;
        // INVENTORY_ACTION_PACKET = 0x30;  // McpeInventoryAction.class
        MCPE_PACKETS[0x31] = McpeContainerOpen.class;
        MCPE_PACKETS[0x32] = McpeContainerClose.class;
        MCPE_PACKETS[0x33] = McpeContainerSetSlot.class;
        MCPE_PACKETS[0x34] = McpeContainerSetData.class;
        MCPE_PACKETS[0x35] = McpeContainerSetContents.class;
        // CRAFTING_DATA_PACKET = 0x36;  // McpeCraftingData.class
	    // CRAFTING_EVENT_PACKET = 0x37; // McpeCraftingEvent.class
        MCPE_PACKETS[0x38] = McpeAdventureSettings.class;
        MCPE_PACKETS[0x39] = McpeBlockEntityData.class;
        // PLAYER_INPUT_PACKET = 0x3a; // McpePlayerInput.class
        MCPE_PACKETS[0x3b] = McpeFullChunkData.class;
        MCPE_PACKETS[0x3c] = McpeSetCommandsEnabled.class;
        // SET_DIFFICULTY_PACKET = 0x3d; // McpeSetDifficulty.class
        MCPE_PACKETS[0x3e] = McpeChangeDimension.class;
        MCPE_PACKETS[0x3f] = McpeSetPlayerGameMode.class;
        MCPE_PACKETS[0x40] = McpePlayerList.class;
        // EVENT_PACKET = 0x41; //McpeEvent.class
        // SPAWN_EXPERIENCE_ORB_PACKET = 0x42;   // McpeSpawnExperienceOrb.class
        // CLIENTBOUND_MAP_ITEM_DATA_PACKET = 0x43;  // McpeClientboundMapItemData.class
        // MAP_INFO_REQUEST_PACKET = 0x44;  // McpeMapInfoRequest.class

        MCPE_PACKETS[0x45] = McpeRequestChunkRadius.class;
        MCPE_PACKETS[0x46] = McpeChunkRadiusUpdated.class;

        // ITEM_FRAME_DROP_ITEM_PACKET = 0x47;  // McpeItemFramDropItem.class
        // REPLACE_SELECTED_ITEM_PACKET = 0x48; // McpeReplaceSelectedItem.class
        // GAME_RULES_CHANGED_PACKET = 0x49;    // McpeGameRulesChanged.class
        // CAMERA_PACKET = 0x4a;                // McpeCamera.class
        // ADD_ITEM_PACKET = 0x4b;              // McpeAddItem.class
        // BOSS_EVENT_PACKET = 0x4c;            // McpeBossEvent.class
        // SHOW_CREDITS_PACKET = 0x4d;          // McpeShowCredits.class
        MCPE_PACKETS[0x4e] = McpeAvailableCommands.class;
        MCPE_PACKETS[0x4f] = McpeCommandStep.class;
        // COMMAND_BLOCK_UPDATE_PACKET = 0x50; // McpeCommandBlockUpdate.class
        // UPDATE_TRADE_PACKET = 0x51; // McpeUpdateTrade.class
        // RESOURCE_PACK_DATA_INFO_PACKET = 0x52; // McpeResourcePackDataInfo.class
        // RESOURCE_PACK_CHUNK_DATA_PACKET = 0x53;// McpeResourcePackChunkData.class
        // RESOURCE_PACK_CHUNK_REQUEST_PACKET = 0x54; //McpeResourcePackChunkRequest.class

        // TRANSFER_PACKET = 0x52 // McpeTransfer.class
        // PLAY_SOUND_PACKET = 0x56; // McpePlaySound.class
        // STOP_SOUND_PACKET = 0x57; // MspeStopSound.class
        // SET_TITLE_PACKET = 0x58; // McpeSetTitle.class

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
