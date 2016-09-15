package com.voxelwind.server.game.level.block.behaviors;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.util.BlockFace;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.block.BehaviorResult;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public class DirtBlockBehavior extends SimpleBlockBehavior {
    public static final DirtBlockBehavior INSTANCE = new DirtBlockBehavior();

    @Override
    public BehaviorResult handleItemInteraction(Server server, Player player, Vector3i against, BlockFace face, ItemStack withItem) {
        if (withItem.getItemType() == ItemTypes.DIAMOND_HOE || withItem.getItemType() == ItemTypes.GOLD_HOE ||
                withItem.getItemType() == ItemTypes.IRON_HOE || withItem.getItemType() == ItemTypes.STONE_HOE ||
                withItem.getItemType() == ItemTypes.WOODEN_HOE) {
            Vector3i adjusted = against.add(face.getOffset());
            Optional<Block> originalBlockOptional = player.getLevel().getBlockIfChunkLoaded(adjusted);
            if (!originalBlockOptional.isPresent()) {
                return BehaviorResult.NOTHING;
            }

            if (BehaviorUtils.replaceBlockState(player, originalBlockOptional.get(), new BasicBlockState(BlockTypes.FARMLAND, null))) {
                return BehaviorResult.REDUCE_DURABILITY;
            } else {
                return BehaviorResult.NOTHING;
            }
        }
        return super.handleItemInteraction(server, player, against, face, withItem);
    }

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        return ImmutableList.of(server.createItemStackBuilder().itemType(BlockTypes.DIRT).amount(1).build());
    }
}
