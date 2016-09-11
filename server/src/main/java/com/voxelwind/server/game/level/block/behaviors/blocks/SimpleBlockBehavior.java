package com.voxelwind.server.game.level.block.behaviors.blocks;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.data.ItemData;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.util.BlockFace;
import com.voxelwind.server.game.level.block.BehaviorResult;
import com.voxelwind.server.game.level.block.BlockBehavior;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SimpleBlockBehavior implements BlockBehavior {
    public static final SimpleBlockBehavior INSTANCE = new SimpleBlockBehavior();

    @Override
    public BehaviorResult handleItemInteraction(Server server, Player player, Vector3i against, BlockFace face, ItemStack withItem) {
        // This is a simple block, so call handlePlacement().
        return withItem.getItemType().isBlock() && handlePlacement(server, player, against, face, withItem) ?
                BehaviorResult.PLACE_BLOCK_AND_REMOVE_ITEM : BehaviorResult.NOTHING;
    }

    @Override
    public boolean handlePlacement(Server server, Player player, Vector3i against, BlockFace face, @Nullable ItemStack withItem) {
        return true;
    }

    @Override
    public boolean handleBreak(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        if (!block.getBlockState().getBlockType().isDiggable()) {
            return true;
        }

        // Continue with normal logic.
        return false;
    }

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        if (!block.getBlockState().getBlockType().isDiggable()) {
            return ImmutableList.of();
        }
        ItemStackBuilder builder = server.createItemStackBuilder()
                .itemType(block.getBlockState().getBlockType())
                .amount(1);
        if (block.getBlockState().getBlockData() instanceof ItemData) {
            builder.itemData((ItemData) block.getBlockState().getBlockData());
        }
        return ImmutableList.of(builder.build());
    }
}
