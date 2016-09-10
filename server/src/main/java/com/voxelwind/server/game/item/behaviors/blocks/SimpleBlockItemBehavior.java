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
import com.voxelwind.server.game.item.BlockBehavior;
import com.voxelwind.server.game.item.behaviors.BehaviorUtils;
import com.voxelwind.server.game.level.block.BasicBlockState;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public class SimpleBlockItemBehavior implements BlockBehavior {
    @Override
    public boolean handleItemInteraction(Server server, Player player, Vector3i against, BlockFace face, ItemStack withItem) {
        // This is a simple block, so call handlePlacement().
        return handlePlacement(server, player, against, face, withItem);
    }

    @Override
    public boolean handlePlacement(Server server, Player player, Vector3i against, BlockFace face, @Nullable ItemStack withItem) {
        // Convert the current item stack into a block state, then set it.
        Preconditions.checkNotNull(withItem, "withItem");
        if (!(withItem.getItemType() instanceof BlockType)) {
            throw new IllegalArgumentException("Item type " + withItem.getItemType().getName() + " is not a block type.");
        }
        BlockType blockType = (BlockType) withItem.getItemType();
        Optional<ItemData> itemData = withItem.getItemData();
        BlockData blockData = null;
        if (itemData.isPresent()) {
            if (itemData.get() instanceof BlockData) {
                blockData = (BlockData) itemData.get();
            }
        }

        return BehaviorUtils.setBlockState(player.getLevel(), against.add(face.getOffset()), new BasicBlockState(blockType, blockData));
    }

    @Override
    public boolean handleBlockInteraction(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        // Nothing to do
        return true;
    }

    @Override
    public boolean handleBreak(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        // Continue with normal logic.
        return false;
    }

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        ItemStackBuilder builder = server.createItemStackBuilder()
                .itemType(block.getBlockState().getBlockType())
                .amount(1);
        if (block.getBlockState().getBlockData() instanceof ItemData) {
            builder.itemData((ItemData) block.getBlockState().getBlockData());
        }
        return ImmutableList.of(builder.build());
    }
}
