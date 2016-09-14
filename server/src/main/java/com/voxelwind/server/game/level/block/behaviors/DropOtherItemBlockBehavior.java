package com.voxelwind.server.game.level.block.behaviors;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

public class DropOtherItemBlockBehavior extends SimpleBlockBehavior {
    private static final Random RANDOM = new Random();
    private final ItemType type;
    private final int minAmount;
    private final int maxAmount;

    public DropOtherItemBlockBehavior(ItemType type) {
        this(type, 1);
    }

    public DropOtherItemBlockBehavior(ItemType type, int amount) {
        this.type = type;
        this.minAmount = amount;
        this.maxAmount = amount;
    }

    public DropOtherItemBlockBehavior(ItemType type, int minAmount, int maxAmount) {
        this.type = type;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        int amount = minAmount == maxAmount ? minAmount : (minAmount + RANDOM.nextInt(maxAmount - minAmount + 1));
        if (amount > 0) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(type)
                    .amount(amount)
                    .build());
        }
        return ImmutableList.of();
    }
}
