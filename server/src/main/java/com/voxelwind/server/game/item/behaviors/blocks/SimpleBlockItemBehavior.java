package com.voxelwind.server.game.item.behaviors.blocks;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.data.ItemData;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockData;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.util.BlockFace;
import com.voxelwind.server.game.item.BehaviorResult;
import com.voxelwind.server.game.item.BlockBehavior;
import com.voxelwind.server.game.item.behaviors.BehaviorUtils;
import com.voxelwind.server.game.level.block.BasicBlockState;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleBlockItemBehavior implements BlockBehavior {
    public static final SimpleBlockItemBehavior INSTANCE = new SimpleBlockItemBehavior();

    @Override
    public BehaviorResult handleItemInteraction(Server server, Player player, Vector3i against, BlockFace face, ItemStack withItem) {
        // This is a simple block, so call handlePlacement().
        return handlePlacement(server, player, against, face, withItem) ? BehaviorResult.REMOVE_ONE_ITEM : BehaviorResult.NOTHING;
    }

    @Override
    public boolean handlePlacement(Server server, Player player, Vector3i against, BlockFace face, @Nullable ItemStack withItem) {
        // Convert the current item stack into a block state, then set it.
        Preconditions.checkNotNull(withItem, "withItem");
        if (!(withItem.getItemType() instanceof BlockType)) {
            throw new IllegalArgumentException("Item type " + withItem.getItemType().getName() + " is not a block type.");
        }

        return BehaviorUtils.setBlockState(player, player.getLevel(), against.add(face.getOffset()), BehaviorUtils.createBlockState(withItem));
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
