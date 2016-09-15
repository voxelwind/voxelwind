package com.voxelwind.server.game.level.block.behaviors;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.level.block.BlockBehavior;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static com.voxelwind.api.game.item.ItemTypes.DIAMOND_PICKAXE;
import static com.voxelwind.api.game.item.ItemTypes.GOLD_PICKAXE;
import static com.voxelwind.api.game.item.ItemTypes.IRON_PICKAXE;

public class DiamondOreBlockBehavior extends SimpleBlockBehavior {
    public static final BlockBehavior INSTANCE = new DiamondOreBlockBehavior();
    private static final List<ItemType> ALLOWED_TO_BREAK = ImmutableList.of(DIAMOND_PICKAXE,
            GOLD_PICKAXE, IRON_PICKAXE);

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        if (withItem != null && ALLOWED_TO_BREAK.contains(withItem.getItemType())) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.DIAMOND)
                    .amount(1)
                    .build());
        }
        return ImmutableList.of();
    }
}
