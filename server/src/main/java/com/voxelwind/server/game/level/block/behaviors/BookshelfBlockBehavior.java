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

public class BookshelfBlockBehavior extends SimpleBlockBehavior {
    public static final BlockBehavior INSTANCE = new BookshelfBlockBehavior();

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.BOOK)
                .amount(3)
                .build());
    }
}
