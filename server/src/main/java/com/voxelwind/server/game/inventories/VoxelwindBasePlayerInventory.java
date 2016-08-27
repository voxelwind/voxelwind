package com.voxelwind.server.game.inventories;

import com.voxelwind.api.game.inventories.PlayerInventory;
import com.voxelwind.server.network.session.PlayerSession;

public class VoxelwindBasePlayerInventory extends VoxelwindBaseInventory implements PlayerInventory {
    private final PlayerSession session;

    public VoxelwindBasePlayerInventory(PlayerSession session) {
        // TODO: Verify
        super(45);
        this.session = session;
    }
}
