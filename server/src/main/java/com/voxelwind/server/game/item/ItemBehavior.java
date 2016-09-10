package com.voxelwind.server.game.item;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.util.BlockFace;

public interface ItemBehavior {
    boolean handleItemInteraction(Server server, Player player, Vector3i against, BlockFace face, ItemStack withItem);
}
