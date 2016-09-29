package com.voxelwind.server.game.level.block.behaviors;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.item.util.ItemTypeUtil;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.level.block.data.TopSnow;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.util.BlockFace;
import com.voxelwind.server.game.level.block.BasicBlockState;
import com.voxelwind.server.game.level.block.BehaviorResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TopSnowBlockBehavior extends SimpleBlockBehavior {
    public static final TopSnowBlockBehavior INSTANCE = new TopSnowBlockBehavior();

    @Override
    public BehaviorResult handleItemInteraction(Server server, Player player, Vector3i against, BlockFace face, ItemStack withItem) {
        if (withItem != null) {
            if (withItem.getItemType() == BlockTypes.TOP_SNOW) {
                Vector3i adjusted = against.add(face.getOffset());
                Optional<Block> originalBlockOptional = player.getLevel().getBlockIfChunkLoaded(adjusted);
                if (!originalBlockOptional.isPresent()) {
                    return BehaviorResult.NOTHING;
                }

                // Add another layer to the snow block if needed
                Block againstBlock = originalBlockOptional.get();
                if (againstBlock.getBlockState().getBlockType() == BlockTypes.TOP_SNOW && againstBlock.getBlockState().getBlockData() instanceof TopSnow) {
                    int layer = ((TopSnow) againstBlock.getBlockState().getBlockData()).getLayers();
                    if (layer >= 7) {
                        // Need to place a new block down.
                        return BehaviorResult.PLACE_BLOCK_AND_REMOVE_ITEM;
                    } else {
                        BlockState newBlockState = new BasicBlockState(BlockTypes.TOP_SNOW, TopSnow.of(layer + 1), null);
                        return BehaviorUtils.replaceBlockState(player, againstBlock, newBlockState) ? BehaviorResult.REMOVE_ONE_ITEM : BehaviorResult.NOTHING;
                    }
                }
            }
        }

        return super.handleItemInteraction(server, player, against, face, withItem);
    }

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        // TODO: Add snow layer support.
        if (withItem != null && ItemTypeUtil.isShovel(withItem.getItemType())) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.SNOWBALL)
                    .amount(1)
                    .build());
        }
        return ImmutableList.of();
    }
}
