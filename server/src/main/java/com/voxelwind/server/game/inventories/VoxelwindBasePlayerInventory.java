package com.voxelwind.server.game.inventories;

import com.voxelwind.api.game.inventories.InventoryType;
import com.voxelwind.api.game.inventories.PlayerInventory;
import com.voxelwind.server.network.session.PlayerSession;

public class VoxelwindBasePlayerInventory extends VoxelwindBaseInventory implements PlayerInventory {
    private final PlayerSession session;

    public VoxelwindBasePlayerInventory(PlayerSession session) {
        // TODO: Verify
        super(InventoryType.PLAYER);
        this.session = session;
        getObserverList().add(session);
    }

    @Override
    public int getUsableInventorySize() {
        // Four slots are reserved for armor, however Voxelwind stores them separately.
        return InventoryType.PLAYER.getInventorySize() - 4;
    }
}
