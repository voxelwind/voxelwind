package com.voxelwind.server.game.level.block.behaviors.blocks;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FarmlandBlockItemBehavior extends SimpleBlockItemBehavior {
    public static final FarmlandBlockItemBehavior INSTANCE = new FarmlandBlockItemBehavior();

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        return ImmutableList.of(server.createItemStackBuilder().itemType(BlockTypes.DIRT).amount(1).build());
    }
}
