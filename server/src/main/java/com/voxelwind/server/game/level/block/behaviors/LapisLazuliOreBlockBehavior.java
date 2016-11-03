package com.voxelwind.server.game.level.block.behaviors;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.item.data.Dyed;
import com.voxelwind.api.game.item.util.ItemTypeUtil;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.util.data.DyeColor;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

public class LapisLazuliOreBlockBehavior extends SimpleBlockBehavior {
    private static final Random RANDOM = new Random();
    public static final LapisLazuliOreBlockBehavior INSTANCE = new LapisLazuliOreBlockBehavior();

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        if (withItem != null) {
            if (ItemTypeUtil.isPickaxe(withItem.getItemType()) && withItem.getItemType() != ItemTypes.WOODEN_PICKAXE) {
                return ImmutableList.of(server.createItemStackBuilder()
                        .itemType(ItemTypes.DYE)
                        .itemData(Dyed.of(DyeColor.BLUE))
                        .amount((4) + RANDOM.nextInt(5))
                        .build());
            }
        }
        return ImmutableList.of();
    }
}
