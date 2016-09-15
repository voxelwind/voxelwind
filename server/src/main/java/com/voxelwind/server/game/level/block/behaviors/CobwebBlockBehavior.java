package com.voxelwind.server.game.level.block.behaviors;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.level.block.BlockBehavior;

import javax.annotation.Nullable;
import java.util.Collection;

public class CobwebBlockBehavior extends SimpleBlockBehavior {
    public static final BlockBehavior INSTANCE = new CobwebBlockBehavior();

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack with) {
        if (with != null) {
            if (with.getItemType() == ItemTypes.SHEARS || with.getItemType() == ItemTypes.IRON_SWORD || with.getItemType() == ItemTypes.GOLD_SWORD ||
                    with.getItemType() == ItemTypes.DIAMOND_SWORD) {
                return ImmutableList.of(server.createItemStackBuilder()
                        .itemType(with.getItemType() == ItemTypes.SHEARS ? block.getBlockState().getBlockType() : ItemTypes.STRING)
                        .amount(1)
                        .build());
            }
        }
        return ImmutableList.of();
    }
}
