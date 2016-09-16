package com.voxelwind.server.game.inventories;

import com.voxelwind.api.game.inventories.InventoryType;
import com.voxelwind.api.game.inventories.PlayerInventory;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.server.network.mcpe.packets.McpeMobEquipment;
import com.voxelwind.server.network.session.PlayerSession;

import java.util.Arrays;
import java.util.Optional;

public class VoxelwindBasePlayerInventory extends VoxelwindBaseInventory implements PlayerInventory {
    private final PlayerSession session;
    private final int[] hotbarLinks = new int[9];
    private int heldHotbarSlot = -1;

    public VoxelwindBasePlayerInventory(PlayerSession session) {
        // TODO: Verify
        super(InventoryType.PLAYER);
        this.session = session;
        getObserverList().add(session);
        Arrays.fill(hotbarLinks, -1);
    }

    @Override
    public int[] getHotbarLinks() {
        return Arrays.copyOf(hotbarLinks, hotbarLinks.length);
    }

    public void setHotbarLink(int hotbarSlot, int inventorySlot) {
        hotbarLinks[hotbarSlot] = inventorySlot;
    }

    @Override
    public int getHeldHotbarSlot() {
        return heldHotbarSlot;
    }

    @Override
    public int getHeldInventorySlot() {
        int slot = heldHotbarSlot;
        if (slot == -1) {
            return -1;
        }
        return hotbarLinks[slot];
    }

    @Override
    public Optional<ItemStack> getStackInHand() {
        int slot = getHeldInventorySlot();
        if (slot == -1) {
            return Optional.empty();
        }
        return getItem(slot);
    }

    public void setHeldHotbarSlot(int hotbarSlot, boolean sendToPlayer) {
        this.heldHotbarSlot = hotbarSlot;

        if (sendToPlayer) {
            McpeMobEquipment equipmentForSelf = new McpeMobEquipment();
            equipmentForSelf.setEntityId(0);
            equipmentForSelf.setHotbarSlot((byte) hotbarSlot);
            equipmentForSelf.setInventorySlot((byte) (hotbarLinks[hotbarSlot] + 9)); // Corrected for the benefit of MCPE
            equipmentForSelf.setStack(getStackInHand().orElse(null));
            session.getMcpeSession().addToSendQueue(equipmentForSelf);
        }

        McpeMobEquipment equipmentForAll = new McpeMobEquipment();
        equipmentForAll.setEntityId(session.getEntityId());
        equipmentForAll.setHotbarSlot((byte) hotbarSlot);
        equipmentForAll.setInventorySlot((byte) hotbarLinks[hotbarSlot]);
        equipmentForAll.setStack(getStackInHand().orElse(null));
        session.getLevel().getPacketManager().queuePacketForViewers(session, equipmentForAll);
    }
}
