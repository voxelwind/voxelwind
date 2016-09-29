package com.voxelwind.server.game.level.block.behaviors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.game.level.block.BlockBehavior;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static com.voxelwind.api.game.item.ItemTypes.*;

public class DroppableBySpecificToolsBlockBehavior extends SimpleBlockBehavior {
    public static final BlockBehavior ALL_PICKAXES = new DroppableBySpecificToolsBlockBehavior(ImmutableList.of(WOODEN_PICKAXE, DIAMOND_PICKAXE,
            GOLDEN_PICKAXE, IRON_PICKAXE, STONE_PICKAXE));
    public static final BlockBehavior ALL_STONE_PICKAXES = new DroppableBySpecificToolsBlockBehavior(ImmutableList.of(DIAMOND_PICKAXE,
            GOLDEN_PICKAXE, IRON_PICKAXE, STONE_PICKAXE));
    public static final BlockBehavior ALL_IRON_PICKAXES = new DroppableBySpecificToolsBlockBehavior(ImmutableList.of(DIAMOND_PICKAXE,
            GOLDEN_PICKAXE, IRON_PICKAXE));
    public static final BlockBehavior ALL_GOLD_PICKAXES = new DroppableBySpecificToolsBlockBehavior(ImmutableList.of(DIAMOND_PICKAXE,
            GOLDEN_PICKAXE));
    public static final BlockBehavior DIAMOND_PICKAXES = new DroppableBySpecificToolsBlockBehavior(ImmutableList.of(DIAMOND_PICKAXE,
            GOLDEN_PICKAXE));
    public static final BlockBehavior SHEARS_ONLY = new DroppableBySpecificToolsBlockBehavior(ImmutableList.of(SHEARS));

    private final List<ItemType> allowedTypes;

    public DroppableBySpecificToolsBlockBehavior(List<ItemType> allowedTypes) {
        Preconditions.checkNotNull(allowedTypes, "allowedTypes");
        this.allowedTypes = allowedTypes;
    }

    @Override
    public Collection<ItemStack> getDrops(Server server, Player player, Block block, @Nullable ItemStack withItem) {
        if (withItem != null && allowedTypes.contains(withItem.getItemType())) {
            return super.getDrops(server, player, block, withItem);
        }
        return ImmutableList.of();
    }
}
