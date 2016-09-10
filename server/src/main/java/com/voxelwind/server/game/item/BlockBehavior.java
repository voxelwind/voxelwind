package com.voxelwind.server.game.item;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.util.BlockFace;

import javax.annotation.Nullable;
import java.util.Collection;

public interface BlockBehavior extends ItemBehavior {
    boolean handlePlacement(Server server, Player player, Vector3i against, BlockFace face, @Nullable ItemStack withItem);
    boolean handleBlockInteraction(Server server, Player player, Block block, @Nullable ItemStack withItem);
    boolean handleBreak(Server server, Player player, Block block, @Nullable ItemStack withItem);
    Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem);
}
