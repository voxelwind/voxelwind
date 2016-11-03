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
import java.util.function.Predicate;

public class DropOtherItemBlockBehavior extends SimpleBlockBehavior {
    private static final Random RANDOM = new Random();
    private final ItemType type;
    private final int minAmount;
    private final int maxAmount;
    private final Predicate<ItemStack> canDrop;

    public DropOtherItemBlockBehavior(ItemType type) {
        this(type, 1);
    }

    public DropOtherItemBlockBehavior(ItemType type, int amount) {
        this.type = type;
        this.minAmount = amount;
        this.maxAmount = amount;
        this.canDrop = s -> true;
    }

    public DropOtherItemBlockBehavior(ItemType type, int minAmount, int maxAmount) {
        this.type = type;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.canDrop = s -> true;
    }

    public DropOtherItemBlockBehavior(ItemType type, int minAmount, int maxAmount, Predicate<ItemStack> canDrop) {
        this.type = type;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.canDrop = canDrop;
    }

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        if (canDrop.test(withItem)) {
            int amount = minAmount == maxAmount ? minAmount : (minAmount + RANDOM.nextInt(maxAmount - minAmount + 1));
            if (amount > 0) {
                return ImmutableList.of(server.createItemStackBuilder()
                        .itemType(type)
                        .amount(amount)
                        .build());
            }
        }
        return ImmutableList.of();
    }
}
