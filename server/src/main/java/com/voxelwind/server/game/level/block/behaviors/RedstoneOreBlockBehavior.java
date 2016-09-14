package com.voxelwind.server.game.level.block.behaviors;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedstoneOreBlockBehavior extends SimpleBlockBehavior {
    public static final RedstoneOreBlockBehavior INSTANCE = new RedstoneOreBlockBehavior();
    private static final Random RANDOM = new Random();

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        if (withItem != null && (withItem.getItemType() == ItemTypes.IRON_PICKAXE || withItem.getItemType() == ItemTypes.DIAMOND_PICKAXE)) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.REDSTONE)
                    .amount(4 + RANDOM.nextInt(2))
                    .build());
        }
        return ImmutableList.of();
    }
}
