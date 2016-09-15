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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetherWartBlockBehavior extends SimpleBlockBehavior {
    public static final NetherWartBlockBehavior INSTANCE = new NetherWartBlockBehavior();

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        // TODO: Implement NetherWart class
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.NETHER_WART)
                .amount(1)
                .build());
    }
}
