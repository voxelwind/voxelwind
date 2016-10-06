package com.voxelwind.server.game.level.block.behaviors;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.level.block.data.Crops;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.game.util.data.BlockFace;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.block.BehaviorResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FarmlandBlockBehavior extends SimpleBlockBehavior {
    public static final FarmlandBlockBehavior INSTANCE = new FarmlandBlockBehavior();

    @Override
    public BehaviorResult handleItemInteraction(Server server, Player player, Vector3i against, BlockFace face, ItemStack withItem) {
        if (face == BlockFace.TOP) {
            Vector3i adjusted = against.add(face.getOffset());
            Optional<Block> originalBlockOptional = player.getLevel().getBlockIfChunkLoaded(adjusted);
            if (!originalBlockOptional.isPresent()) {
                return BehaviorResult.NOTHING;
            }

            // If a block is already on the farmland, don't proceed.
            if (originalBlockOptional.get().getBlockState().getBlockType() != BlockTypes.AIR) {
                return BehaviorResult.NOTHING;
            }

            // Seeds
            if (withItem.getItemType() == ItemTypes.SEEDS) {
                return BehaviorUtils.setBlockState(player, adjusted, new BasicBlockState(BlockTypes.CROPS, Crops.NEW, null)) ?
                        BehaviorResult.REMOVE_ONE_ITEM : BehaviorResult.NOTHING;
            }

            // TODO: Other seeds
            if (withItem.getItemType().isBlock()) {
                // Destroy the farmland.
                if (BehaviorUtils.replaceBlockState(player, originalBlockOptional.get(), new BasicBlockState(BlockTypes.DIRT, null, null))) {
                    return BehaviorResult.PLACE_BLOCK_AND_REMOVE_ITEM;
                } else {
                    return BehaviorResult.NOTHING;
                }
            }
        }
        return super.handleItemInteraction(server, player, against, face, withItem);
    }

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        return ImmutableList.of(server.createItemStackBuilder().itemType(BlockTypes.DIRT).amount(1).build());
    }
}
