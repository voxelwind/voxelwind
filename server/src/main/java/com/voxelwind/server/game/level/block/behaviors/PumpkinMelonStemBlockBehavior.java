package com.voxelwind.server.game.level.block.behaviors;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.data.Crops;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

public class PumpkinMelonStemBlockBehavior extends SimpleBlockBehavior {
    public static final PumpkinMelonStemBlockBehavior PUMPKIN = new PumpkinMelonStemBlockBehavior(ItemTypes.PUMPKIN_SEEDS);
    public static final PumpkinMelonStemBlockBehavior MELON = new PumpkinMelonStemBlockBehavior(ItemTypes.MELON_SEEDS);

    private static final Random RANDOM = new Random();
    private final ItemType type;

    private PumpkinMelonStemBlockBehavior(ItemType type) {
        this.type = type;
    }

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        Crops crop = (Crops) block.getBlockState().getBlockData();
        if (crop.isFullyGrown()) {
            int amount = RANDOM.nextInt(4);
            if (amount == 0) return ImmutableList.of();
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(type)
                    .amount(amount)
                    .build());
        } else {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(type)
                    .amount(1)
                    .build());
        }
    }
}
