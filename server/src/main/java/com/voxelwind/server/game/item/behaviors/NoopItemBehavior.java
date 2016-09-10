package com.voxelwind.server.game.item.behaviors;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.util.BlockFace;
import com.voxelwind.server.game.item.ItemBehavior;

/**
 * This behavior does nothing when the item is interacted with.
 */
public class NoopItemBehavior implements ItemBehavior {
    @Override
    public boolean handleItemInteraction(Server server, Player player, Vector3i against, BlockFace face, ItemStack withItem) {
        return true;
    }
}
