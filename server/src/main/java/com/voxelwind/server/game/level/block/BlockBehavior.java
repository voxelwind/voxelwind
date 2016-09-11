package com.voxelwind.server.game.level.block;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.util.BlockFace;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public interface BlockBehavior {
    boolean handlePlacement(Server server, Player player, Vector3i against, BlockFace face, @Nullable ItemStack withItem);
    BehaviorResult handleItemInteraction(Server server, Player player, Vector3i against, BlockFace face, ItemStack withItem);
    boolean handleBreak(Server server, Player player, Block block, @Nullable ItemStack withItem);
    Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem);
    default Optional<BlockEntity> createBlockEntity() {
        return Optional.empty();
    }
}
