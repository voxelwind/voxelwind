package com.voxelwind.api.game.level.block;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.item.data.Dyed;
import com.voxelwind.api.game.item.data.ItemData;
import com.voxelwind.api.game.item.util.ItemTypeUtil;
import com.voxelwind.api.game.level.block.data.Crops;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.util.DyeColor;
import lombok.Builder;

import java.util.*;

/**
 * This class contains all block types recognized by Voxelwind and Pocket Edition.
 */
public class BlockTypes {
    private static Map<Integer, BlockType> BY_ID = new HashMap<>();
    private static final Random RANDOM = new Random();

    public static final BlockType AIR = IntBlock.builder().id(0).maxStackSize(0).diggable(false).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType STONE = IntBlock.builder().id(1).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfForInDrop.ALL_PICKAXES).build();
    public static final BlockType DIRT = IntBlock.builder().id(3).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GRASS_BLOCK = IntBlock.builder().id(2).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        // TODO: Handle silk touch
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(DIRT)
                .amount(1)
                .build());
    }).build();
    public static final BlockType COBBLESTONE = IntBlock.builder().id(4).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WOOD_PLANKS = IntBlock.builder().id(5).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType SAPLING = IntBlock.builder().id(6).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BEDROCK = IntBlock.builder().id(7).maxStackSize(64).diggable(false).transparent(false).emitLight(0).filterLight(15).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType WATER = IntBlock.builder().id(8).maxStackSize(0).diggable(false).transparent(true).emitLight(0).filterLight(2).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType STATIONARY_WATER = IntBlock.builder().id(9).maxStackSize(0).diggable(false).transparent(true).emitLight(0).filterLight(2).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType LAVA = IntBlock.builder().id(10).maxStackSize(0).diggable(false).transparent(true).emitLight(15).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType STATIONARY_LAVA = IntBlock.builder().id(11).maxStackSize(0).diggable(false).transparent(true).emitLight(15).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType SAND = IntBlock.builder().id(12).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GRAVEL = IntBlock.builder().id(13).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GOLD_ORE = IntBlock.builder().id(14).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType IRON_ORE = IntBlock.builder().id(15).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType COAL_ORE = IntBlock.builder().id(16).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        if (ItemTypeUtil.isPickaxe(with.getItemType())) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.COAL)
                    .amount(1)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType WOOD = IntBlock.builder().id(17).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType LEAVES = IntBlock.builder().id(18).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        if (with.getItemType() == ItemTypes.SHEARS) {
            return ImmutableList.of(tryExact(server, block));
        }

        // TODO: Drop saplings and apples. (http://minecraft.gamepedia.com/Leaves)
        return ImmutableList.of();
    }).build();
    public static final BlockType SPONGE = IntBlock.builder().id(19).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GLASS = IntBlock.builder().id(20).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType LAPIS_LAZULI_ORE = IntBlock.builder().id(21).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, i2, i3) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.DYE)
                .itemData(Dyed.of(DyeColor.BLUE))
                .amount((4) + RANDOM.nextInt(5))
                .build());
    }).build();
    public static final BlockType LAPIS_LAZULI_BLOCK = IntBlock.builder().id(22).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType DISPENSER = IntBlock.builder().id(23).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType SANDSTONE = IntBlock.builder().id(24).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NOTE_BLOCK = IntBlock.builder().id(25).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BED = IntBlock.builder().id(26).maxStackSize(1).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.BED)
                .amount(1)
                .build());
    }).build();
    public static final BlockType POWERED_RAIL = IntBlock.builder().id(27).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType DETECTOR_RAIL = IntBlock.builder().id(28).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType COBWEB = IntBlock.builder().id(30).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        if (with.getItemType() == ItemTypes.SHEARS || with.getItemType() == ItemTypes.IRON_SWORD || with.getItemType() == ItemTypes.GOLD_SWORD ||
                with.getItemType() == ItemTypes.DIAMOND_SWORD) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(with.getItemType() == ItemTypes.SHEARS ? block.getBlockState().getBlockType() : ItemTypes.STRING)
                    .amount(1)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType TALL_GRASS = IntBlock.builder().id(31).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        if (with.getItemType() == ItemTypes.SHEARS) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(with.getItemType() == ItemTypes.SHEARS ? block.getBlockState().getBlockType() : ItemTypes.STRING)
                    .amount(1)
                    .build());
        } else {
            if (RANDOM.nextInt(15) == 0) {
                return ImmutableList.of(server.createItemStackBuilder()
                        .itemType(ItemTypes.SEEDS)
                        .amount(1)
                        .build());
            } else {
                return ImmutableList.of();
            }
        }
    }).build();
    public static final BlockType DEAD_BUSH = IntBlock.builder().id(32).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType WOOL = IntBlock.builder().id(35).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType DANDELION = IntBlock.builder().id(37).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType FLOWER = IntBlock.builder().id(38).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BROWN_MUSHROOM = IntBlock.builder().id(39).maxStackSize(64).diggable(true).transparent(false).emitLight(1).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType RED_MUSHROOM = IntBlock.builder().id(40).maxStackSize(64).diggable(true).transparent(false).emitLight(1).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GOLD_BLOCK = IntBlock.builder().id(41).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType IRON_BLOCK = IntBlock.builder().id(42).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType DOUBLE_STONE_SLAB = IntBlock.builder().id(43).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfForInDrop.ALL_PICKAXES).build();
    public static final BlockType STONE_SLAB = IntBlock.builder().id(44).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfForInDrop.ALL_PICKAXES).build();
    public static final BlockType BRICKS = IntBlock.builder().id(45).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType TNT = IntBlock.builder().id(46).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BOOKSHELF = IntBlock.builder().id(47).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.BOOK)
                .amount(3)
                .build());
    }).build();
    public static final BlockType MOSS_STONE = IntBlock.builder().id(48).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType OBSIDIAN = IntBlock.builder().id(49).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType TORCH = IntBlock.builder().id(50).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType FIRE = IntBlock.builder().id(51).maxStackSize(0).diggable(true).transparent(true).emitLight(15).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType MONSTER_SPAWNER = IntBlock.builder().id(52).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType OAK_WOOD_STAIRS = IntBlock.builder().id(53).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CHEST = IntBlock.builder().id(54).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_WIRE = IntBlock.builder().id(55).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.REDSTONE)
                .amount(1)
                .build());
    }).build();
    public static final BlockType DIAMOND_ORE = IntBlock.builder().id(56).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.DIAMOND)
                .amount(1)
                .build());
    }).build();
    public static final BlockType DIAMOND_BLOCK = IntBlock.builder().id(57).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CRAFTING_TABLE = IntBlock.builder().id(58).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CROPS = IntBlock.builder().id(59).maxStackSize(0).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
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
    }).blockDataClass(Crops.class).fromBlockMetadata(Crops::ofStage).build();
    public static final BlockType FARMLAND = IntBlock.builder().id(60).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(DIRT)
                .amount(1)
                .build());
    }).build();
    public static final BlockType FURNACE = IntBlock.builder().id(61).maxStackSize(64).diggable(true).transparent(true).emitLight(13).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BURNING_FURNACE = IntBlock.builder().id(62).maxStackSize(64).diggable(true).transparent(true).emitLight(13).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(FURNACE)
                .amount(1)
                .build());
    }).build();
    public static final BlockType SIGN = IntBlock.builder().id(63).maxStackSize(16).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WOODEN_DOOR = IntBlock.builder().id(64).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType LADDER = IntBlock.builder().id(65).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType RAIL = IntBlock.builder().id(66).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType COBBLESTONE_STAIRS = IntBlock.builder().id(67).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WALL_SIGN = IntBlock.builder().id(68).maxStackSize(16).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType LEVER = IntBlock.builder().id(69).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType STONE_PRESSURE_PLATE = IntBlock.builder().id(70).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType IRON_DOOR = IntBlock.builder().id(71).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WOODEN_PRESSURE_PLATE = IntBlock.builder().id(72).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_ORE = IntBlock.builder().id(73).maxStackSize(64).diggable(true).transparent(true).emitLight(9).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.REDSTONE)
                .amount(4 + RANDOM.nextInt(2))
                .build());
    }).build();
    public static final BlockType GLOWING_REDSTONE_ORE = IntBlock.builder().id(74).maxStackSize(64).diggable(true).transparent(true).emitLight(9).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.REDSTONE)
                .amount(4 + RANDOM.nextInt(2))
                .build());
    }).build();
    public static final BlockType REDSTONE_TORCH = IntBlock.builder().id(75).maxStackSize(64).diggable(true).transparent(true).emitLight(7).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_TORCH_ACTIVE = IntBlock.builder().id(76).maxStackSize(64).diggable(true).transparent(true).emitLight(7).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(REDSTONE_TORCH)
                .amount(1)
                .build());
    }).build();
    public static final BlockType STONE_BUTTON = IntBlock.builder().id(77).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType TOP_SNOW = IntBlock.builder().id(78).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        if (ItemTypeUtil.isShovel(with.getItemType()) && RANDOM.nextBoolean()) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.SNOWBALL)
                    .amount(1)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType ICE = IntBlock.builder().id(79).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType SNOW = IntBlock.builder().id(80).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        if (ItemTypeUtil.isShovel(with.getItemType()) && RANDOM.nextBoolean()) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.SNOWBALL)
                    .amount(4)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType CACTUS = IntBlock.builder().id(81).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CLAY = IntBlock.builder().id(82).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        if (ItemTypeUtil.isShovel(with.getItemType()) && RANDOM.nextBoolean()) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.CLAY)
                    .amount(4)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType SUGAR_CANE = IntBlock.builder().id(83).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType FENCE = IntBlock.builder().id(85).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType PUMPKIN = IntBlock.builder().id(86).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NETHERRACK = IntBlock.builder().id(87).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType SOUL_SAND = IntBlock.builder().id(88).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GLOWSTONE = IntBlock.builder().id(89).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        // TODO: Handle silk touch
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.GLOWSTONE_DUST)
                .amount(2 + RANDOM.nextInt(3))
                .build());
    }).build();
    public static final BlockType PORTAL = IntBlock.builder().id(90).maxStackSize(0).diggable(false).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType JACK_OLANTERN = IntBlock.builder().id(91).maxStackSize(64).diggable(true).transparent(true).emitLight(15).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CAKE = IntBlock.builder().id(92).maxStackSize(1).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType REDSTONE_REPEATER = IntBlock.builder().id(93).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_REPEATER_ACTIVE = IntBlock.builder().id(94).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType INVISIBLE_BEDROCK = IntBlock.builder().id(95).maxStackSize(64).diggable(false).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType TRAPDOOR = IntBlock.builder().id(96).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType MONSTER_EGG = IntBlock.builder().id(97).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType STONE_BRICK = IntBlock.builder().id(98).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BROWN_MUSHROOM_BLOCK = IntBlock.builder().id(99).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        int amount = RANDOM.nextInt(3);
        if (amount > 0) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(BROWN_MUSHROOM)
                    .amount(amount)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType RED_MUSHROOM_BLOCK = IntBlock.builder().id(100).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        int amount = RANDOM.nextInt(3);
        if (amount > 0) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(RED_MUSHROOM)
                    .amount(amount)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType IRON_BARS = IntBlock.builder().id(101).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GLASS_PANE = IntBlock.builder().id(102).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType MELON = IntBlock.builder().id(103).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.MELON)
                .amount(RANDOM.nextInt(5) + 3)
                .build());
    }).build();
    public static final BlockType PUMPKIN_STEM = IntBlock.builder().id(104).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        Crops crop = (Crops) block.getBlockState().getBlockData();
        if (crop.isFullyGrown()) {
            int amount = RANDOM.nextInt(4);
            if (amount == 0) return ImmutableList.of();
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.PUMPKIN_SEEDS)
                    .amount(amount)
                    .build());
        } else {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.PUMPKIN_SEEDS)
                    .amount(1)
                    .build());
        }
    }).fromBlockMetadata(Crops::ofStage).blockDataClass(Crops.class).build();
    public static final BlockType MELON_STEM = IntBlock.builder().id(105).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        Crops crop = (Crops) block.getBlockState().getBlockData();
        if (crop.isFullyGrown()) {
            int amount = RANDOM.nextInt(4);
            if (amount == 0) return ImmutableList.of();
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.MELON_SEEDS)
                    .amount(amount)
                    .build());
        } else {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.MELON_SEEDS)
                    .amount(1)
                    .build());
        }
    }).fromBlockMetadata(Crops::ofStage).blockDataClass(Crops.class).build();
    public static final BlockType VINES = IntBlock.builder().id(106).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfForInDrop.SHEARS_ONLY).build();
    public static final BlockType FENCE_GATE = IntBlock.builder().id(107).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BRICK_STAIRS = IntBlock.builder().id(108).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType STONE_BRICK_STAIRS = IntBlock.builder().id(109).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType MYCELIUM = IntBlock.builder().id(110).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        // TODO: Silk touch
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(DIRT)
                .amount(1)
                .build());
    }).build();
    public static final BlockType LILY_PAD = IntBlock.builder().id(111).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NETHER_BRICK = IntBlock.builder().id(112).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NETHER_BRICK_FENCE = IntBlock.builder().id(113).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NETHER_BRICK_STAIRS = IntBlock.builder().id(114).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NETHER_WART = IntBlock.builder().id(115).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        // TODO: Implement NetherWart class
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.NETHER_WART)
                .amount(1)
                .build());
    }).build();
    public static final BlockType ENCHANTMENT_TABLE = IntBlock.builder().id(116).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BREWING_STAND = IntBlock.builder().id(117).maxStackSize(64).diggable(true).transparent(true).emitLight(1).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CAULDRON = IntBlock.builder().id(118).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType END_PORTAL_FRAME = IntBlock.builder().id(120).maxStackSize(64).diggable(false).transparent(true).emitLight(1).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType END_STONE = IntBlock.builder().id(121).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_LAMP = IntBlock.builder().id(122).maxStackSize(64).diggable(true).transparent(true).emitLight(15).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_LAMP_ACTIVE = IntBlock.builder().id(123).maxStackSize(64).diggable(true).transparent(true).emitLight(15).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType ACTIVATOR_RAIL = IntBlock.builder().id(126).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType COCOA = IntBlock.builder().id(127).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        // TODO: Implement cocoa class.
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.DYE)
                .itemData(Dyed.of(DyeColor.BROWN))
                .amount(1)
                .build());
    }).build();
    public static final BlockType SANDSTONE_STAIRS = IntBlock.builder().id(128).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType EMERALD_ORE = IntBlock.builder().id(129).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.EMERALD)
                .amount(1)
                .build());
    }).build();
    public static final BlockType TRIPWIRE_HOOK = IntBlock.builder().id(131).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType TRIPWIRE = IntBlock.builder().id(132).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType EMERALD_BLOCK = IntBlock.builder().id(133).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType SPRUCE_WOOD_STAIRS = IntBlock.builder().id(134).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BIRCH_WOOD_STAIRS = IntBlock.builder().id(135).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType JUNGLE_WOOD_STAIRS = IntBlock.builder().id(136).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType COBBLESTONE_WALL = IntBlock.builder().id(139).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType FLOWER_POT = IntBlock.builder().id(140).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CARROTS = IntBlock.builder().id(141).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType POTATO = IntBlock.builder().id(142).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WOODEN_BUTTON = IntBlock.builder().id(143).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType MOB_HEAD = IntBlock.builder().id(144).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType ANVIL = IntBlock.builder().id(145).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType TRAPPED_CHEST = IntBlock.builder().id(146).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WEIGHTED_PRESSURE_PLATE_LIGHT = IntBlock.builder().id(147).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WEIGHTED_PRESSURE_PLATE_HEAVY = IntBlock.builder().id(148).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType DAYLIGHT_SENSOR = IntBlock.builder().id(151).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_BLOCK = IntBlock.builder().id(152).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NETHER_QUARTZ_ORE = IntBlock.builder().id(153).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.NETHER_QUARTZ)
                .amount(1)
                .build());
    }).build();
    public static final BlockType QUARTZ_BLOCK = IntBlock.builder().id(155).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType QUARTZ_STAIRS = IntBlock.builder().id(156).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    // TODO: Verify next two
    public static final BlockType WOODEN_DOUBLE_SLAB = IntBlock.builder().id(157).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WOODEN_SLAB = IntBlock.builder().id(158).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType STAINED_CLAY = IntBlock.builder().id(159).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType ACACIA_LEAVES = IntBlock.builder().id(161).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        // TODO: Handle saplings
        return ImmutableList.of();
    }).build();
    public static final BlockType ACACIA_WOOD = IntBlock.builder().id(162).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType ACACIA_WOOD_STAIRS = IntBlock.builder().id(163).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType DARK_OAK_WOOD_STAIRS = IntBlock.builder().id(164).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType IRON_TRAPDOOR = IntBlock.builder().id(167).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType HAY_BALE = IntBlock.builder().id(170).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CARPET = IntBlock.builder().id(171).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType HARDENED_CLAY = IntBlock.builder().id(172).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType COAL_BLOCK = IntBlock.builder().id(173).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType PACKED_ICE = IntBlock.builder().id(174).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType SUNFLOWER = IntBlock.builder().id(175).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType INVERTED_DAYLIGHT_SENSOR = IntBlock.builder().id(178).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType SPRUCE_FENCE_GATE = IntBlock.builder().id(183).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BIRCH_FENCE_GATE = IntBlock.builder().id(184).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType JUNGLE_FENCE_GATE = IntBlock.builder().id(185).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType DARK_OAK_FENCE_GATE = IntBlock.builder().id(186).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType ACACIA_FENCE_GATE = IntBlock.builder().id(187).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GRASS_PATH = IntBlock.builder().id(198).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType ITEM_FRAME = IntBlock.builder().id(199).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType PODZOL = IntBlock.builder().id(243).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        // TODO: Handle silk touch
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(DIRT)
                .amount(1)
                .build());
    }).build();
    public static final BlockType BEETROOT = IntBlock.builder().id(244).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType STONECUTTER = IntBlock.builder().id(245).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GLOWING_OBSIDIAN = IntBlock.builder().id(246).maxStackSize(64).diggable(true).transparent(false).emitLight(12).filterLight(15).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType NETHER_REACTOR_CORE = IntBlock.builder().id(247).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        // TODO: handle this
        return ImmutableList.of();
    }).build();

    public static BlockType forId(int data) {
        BlockType type = BY_ID.get(data);
        if (type == null) {
            throw new IllegalArgumentException("ID is not valid.");
        }
        return type;
    }

    private interface DroppedHandler {
        Collection<ItemStack> drop(Server server, Block block, ItemStack with);
    }

    private static ItemStack tryExact(Server server, Block block) {
        ItemStackBuilder builder = server.createItemStackBuilder()
                .itemType(block.getBlockState().getBlockType())
                .amount(1);
        if (block.getBlockState().getBlockData() instanceof ItemData) {
            builder.itemData((ItemData) block.getBlockState().getBlockData());
        }
        return builder.build();
    }

    private static class SelfDrop implements DroppedHandler {
        private static SelfDrop INSTANCE = new SelfDrop();

        @Override
        public Collection<ItemStack> drop(Server server, Block block, ItemStack with) {
            ItemStackBuilder builder = server.createItemStackBuilder().itemType(block.getBlockState().getBlockType()).amount(1);
            if (block.getBlockState().getBlockData() != null && block.getBlockState().getBlockData() instanceof BlockData) {
                builder.itemData((ItemData) block.getBlockState().getBlockData());
            }
            return ImmutableList.of(builder.build());
        }
    }

    private static class NothingDrop implements DroppedHandler {
        private static NothingDrop INSTANCE = new NothingDrop();

        @Override
        public Collection<ItemStack> drop(Server server, Block block, ItemStack with) {
            return ImmutableList.of();
        }
    }

    private static class SelfForInDrop implements DroppedHandler {
        private final Collection<ItemType> forItems;

        public static final SelfForInDrop ALL_PICKAXES = new SelfForInDrop(ImmutableList.of(
                ItemTypes.WOODEN_PICKAXE, ItemTypes.STONE_PICKAXE, ItemTypes.IRON_PICKAXE, ItemTypes.GOLD_PICKAXE, ItemTypes.DIAMOND_PICKAXE
        ));
        public static final SelfForInDrop SHEARS_ONLY = new SelfForInDrop(ImmutableList.of(ItemTypes.SHEARS));

        private SelfForInDrop(Collection<ItemType> forItems) {
            this.forItems = forItems;
        }

        @Override
        public Collection<ItemStack> drop(Server server, Block block, ItemStack with) {
            if (forItems.contains(with.getItemType())) {
                return ImmutableList.of(tryExact(server, block));
            }
            return ImmutableList.of();
        }
    }

    private interface FromBlockMetadata {
        BlockData of(short data);
    }

    @Builder
    private static class IntBlock implements BlockType {
        private final int id;
        private final int maxStackSize;
        private final boolean diggable;
        private final boolean transparent;
        private final int emitLight;
        private final int filterLight;
        private final DroppedHandler dropHandler;
        private final Class<? extends BlockData> blockDataClass;
        private final ItemTypes.FromMetadata fromMetadata;
        private final FromBlockMetadata fromBlockMetadata;

        public IntBlock(int id, int maxStackSize, boolean diggable, boolean transparent, int emitLight, int filterLight, DroppedHandler dropHandler, Class<? extends BlockData> aClass, ItemTypes.FromMetadata fromMetadata, FromBlockMetadata fromBlockMetadata) {
            this.id = id;
            this.maxStackSize = maxStackSize;
            this.diggable = diggable;
            this.transparent = transparent;
            this.emitLight = emitLight;
            this.filterLight = filterLight;
            this.dropHandler = dropHandler;
            this.blockDataClass = aClass;
            this.fromMetadata = fromMetadata;
            this.fromBlockMetadata = fromBlockMetadata;

            BY_ID.put(id, this);
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public Class<? extends ItemData> getMaterialDataClass() {
            if (ItemData.class.isAssignableFrom(blockDataClass)) {
                return (Class<? extends ItemData>) blockDataClass;
            }
            return null;
        }

        @Override
        public int getMaximumStackSize() {
            return maxStackSize;
        }

        @Override
        public Optional<ItemData> createDataFor(short metadata) {
            if (fromMetadata != null) {
                return Optional.of(fromMetadata.of(metadata));
            }
            return Optional.empty();
        }

        @Override
        public Optional<BlockData> createBlockDataFor(short metadata) {
            if (fromMetadata != null) {
                return Optional.of(fromBlockMetadata.of(metadata));
            }
            return Optional.empty();
        }

        @Override
        public boolean isDiggable() {
            return diggable;
        }

        @Override
        public boolean isTransparent() {
            return transparent;
        }

        @Override
        public int emitsLight() {
            return emitLight;
        }

        @Override
        public int filtersLight() {
            return filterLight;
        }

        @Override
        public Collection<ItemStack> getDrops(Server server, Block block, ItemStack with) {
            return dropHandler.drop(server, block, with);
        }

        @Override
        public Class<? extends BlockData> getBlockDataClass() {
            return blockDataClass;
        }
    }
}
