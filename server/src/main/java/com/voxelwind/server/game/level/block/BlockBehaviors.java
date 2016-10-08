package com.voxelwind.server.game.level.block;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.item.util.ItemTypeUtil;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.server.game.level.block.behaviors.*;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class BlockBehaviors {
    private static final Map<BlockType, BlockBehavior> SPECIAL_BEHAVIORS;

    static {
        SPECIAL_BEHAVIORS = ImmutableMap.<BlockType, BlockBehavior>builder()
                .put(BlockTypes.DIRT, DirtBlockBehavior.INSTANCE)
                .put(BlockTypes.GRASS_BLOCK, DirtBlockBehavior.INSTANCE)
                .put(BlockTypes.GRASS_PATH, DirtBlockBehavior.INSTANCE)
                .put(BlockTypes.MYCELIUM, DirtBlockBehavior.INSTANCE)
                .put(BlockTypes.FARMLAND, FarmlandBlockBehavior.INSTANCE)
                .put(BlockTypes.STONE, DroppableBySpecificToolsBlockBehavior.ALL_PICKAXES)
                .put(BlockTypes.COAL_ORE, DroppableBySpecificToolsBlockBehavior.ALL_PICKAXES)
                .put(BlockTypes.COAL_BLOCK, DroppableBySpecificToolsBlockBehavior.ALL_PICKAXES)
                .put(BlockTypes.IRON_ORE, DroppableBySpecificToolsBlockBehavior.ALL_STONE_PICKAXES)
                .put(BlockTypes.IRON_BLOCK, DroppableBySpecificToolsBlockBehavior.ALL_STONE_PICKAXES)
                .put(BlockTypes.GOLD_ORE, DroppableBySpecificToolsBlockBehavior.ALL_IRON_PICKAXES)
                .put(BlockTypes.GOLD_BLOCK, DroppableBySpecificToolsBlockBehavior.ALL_IRON_PICKAXES)
                .put(BlockTypes.LAPIS_LAZULI_ORE, LapisLazuliOreBlockBehavior.INSTANCE)
                .put(BlockTypes.LAPIS_LAZULI_BLOCK, DroppableBySpecificToolsBlockBehavior.ALL_STONE_PICKAXES)
                .put(BlockTypes.DIAMOND_ORE, DiamondOreBlockBehavior.INSTANCE)
                .put(BlockTypes.DIAMOND_BLOCK, DroppableBySpecificToolsBlockBehavior.ALL_IRON_PICKAXES)
                .put(BlockTypes.LEAVES, DroppableBySpecificToolsBlockBehavior.SHEARS_ONLY) // TODO: Handle this better.
                .put(BlockTypes.COBWEB, CobwebBlockBehavior.INSTANCE)
                .put(BlockTypes.BOOKSHELF, new DropOtherItemBlockBehavior(ItemTypes.BOOK, 3))
                .put(BlockTypes.CROPS, CropsBlockBehavior.INSTANCE)
                .put(BlockTypes.REDSTONE_WIRE, new DropOtherItemBlockBehavior(ItemTypes.REDSTONE, 1))
                .put(BlockTypes.REDSTONE_LAMP_ACTIVE, new DropOtherItemBlockBehavior(BlockTypes.REDSTONE_LAMP, 1))
                .put(BlockTypes.REDSTONE_REPEATER_ACTIVE, new DropOtherItemBlockBehavior(BlockTypes.REDSTONE_REPEATER, 1))
                .put(BlockTypes.REDSTONE_TORCH, new DropOtherItemBlockBehavior(BlockTypes.REDSTONE_TORCH_ACTIVE, 1))
                .put(BlockTypes.TRIPWIRE, new DropOtherItemBlockBehavior(ItemTypes.STRING, 1))
                .put(BlockTypes.SIGN, new DropOtherItemBlockBehavior(ItemTypes.SIGN, 1))
                .put(BlockTypes.WALL_SIGN, new DropOtherItemBlockBehavior(ItemTypes.SIGN, 1))
                .put(BlockTypes.REDSTONE_ORE, RedstoneOreBlockBehavior.INSTANCE)
                .put(BlockTypes.GLOWING_REDSTONE_ORE, RedstoneOreBlockBehavior.INSTANCE)
                .put(BlockTypes.SNOW, SnowBlockBehavior.INSTANCE)
                .put(BlockTypes.TOP_SNOW, TopSnowBlockBehavior.INSTANCE)
                .put(BlockTypes.CLAY, ClayBlockBehavior.INSTANCE)
                .put(BlockTypes.BROWN_MUSHROOM_BLOCK, new DropOtherItemBlockBehavior(BlockTypes.BROWN_MUSHROOM, 0, 2))
                .put(BlockTypes.RED_MUSHROOM_BLOCK, new DropOtherItemBlockBehavior(BlockTypes.RED_MUSHROOM, 0, 2))
                .put(BlockTypes.MELON, new DropOtherItemBlockBehavior(ItemTypes.MELON, 3, 7))
                .put(BlockTypes.MELON_STEM, PumpkinMelonStemBlockBehavior.MELON)
                .put(BlockTypes.PUMPKIN_STEM, PumpkinMelonStemBlockBehavior.PUMPKIN)
                .put(BlockTypes.NETHER_WART, NetherWartBlockBehavior.INSTANCE)
                .put(BlockTypes.EMERALD_ORE, new DropOtherItemBlockBehavior(ItemTypes.EMERALD, 1, 1, stack -> stack != null &&
                        stack.getItemType() == ItemTypes.IRON_PICKAXE && stack.getItemType() == ItemTypes.DIAMOND_PICKAXE))
                .put(BlockTypes.NETHER_QUARTZ_ORE, new DropOtherItemBlockBehavior(ItemTypes.NETHER_QUARTZ, 1, 1, s -> s != null && ItemTypeUtil.isPickaxe(s.getItemType())))
                .put(BlockTypes.GLASS, DropNothingBlockBehavior.INSTANCE)
                .put(BlockTypes.GLASS_PANE, DropNothingBlockBehavior.INSTANCE)
                .put(BlockTypes.DEAD_BUSH, DropNothingBlockBehavior.INSTANCE)
                .put(BlockTypes.MONSTER_EGG, DropNothingBlockBehavior.INSTANCE)
                .put(BlockTypes.MONSTER_SPAWNER, DropNothingBlockBehavior.INSTANCE)
                .put(BlockTypes.CAKE, DropNothingBlockBehavior.INSTANCE)
                .put(BlockTypes.WOOD, new LogBlockBehavior())
                .put(BlockTypes.ACACIA_WOOD, new LogBlockBehavior())
                .build();
    }

    public static BlockBehavior getBlockBehavior(BlockType type) {
        Preconditions.checkNotNull(type, "type");
        BlockBehavior behavior = SPECIAL_BEHAVIORS.get(type);
        if (behavior == null) {
            return SimpleBlockBehavior.INSTANCE;
        }
        return behavior;
    }
}
