package com.voxelwind.server.game.level.block.behaviors;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.data.Crops;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CropsBlockBehavior extends SimpleBlockBehavior {
    public static final CropsBlockBehavior INSTANCE = new CropsBlockBehavior();
    private static final Random RANDOM = new Random();

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        if (block.getBlockState().getBlockData() instanceof Crops) {
            Crops crops = (Crops) block.getBlockState().getBlockData();
            if (crops.isFullyGrown()) {
                int seedAmount = RANDOM.nextInt(4);
                List<ItemStack> stacks = new ArrayList<>();
                if (seedAmount != 0) {
                    stacks.add(server.createItemStackBuilder()
                            .itemType(ItemTypes.SEEDS)
                            .amount(seedAmount)
                            .build());
                }
                stacks.add(server.createItemStackBuilder()
                        .itemType(ItemTypes.WHEAT)
                        .amount(1)
                        .build());
                return stacks;
            } else {
                return ImmutableList.of(server.createItemStackBuilder()
                        .itemType(ItemTypes.SEEDS)
                        .amount(1)
                        .build());
            }
        }
        return ImmutableList.of();
    }
}
