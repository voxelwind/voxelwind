package com.voxelwind.server.game.level.block.behaviors;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;

import javax.annotation.Nullable;
import java.util.Collection;

public class DropOtherItemBlockBehavior extends SimpleBlockBehavior {
    private final ItemType type;
    private final int amount;

    public DropOtherItemBlockBehavior(ItemType type) {
        this(type, 1);
    }

    public DropOtherItemBlockBehavior(ItemType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        return ImmutableList.of(server.createItemStackBuilder().itemType(type).amount(amount).build());
    }
}
