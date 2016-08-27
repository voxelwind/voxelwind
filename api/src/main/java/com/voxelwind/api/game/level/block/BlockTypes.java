package com.voxelwind.api.game.level.block;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.item.data.Dyed;
import com.voxelwind.api.game.item.data.ItemData;
import com.voxelwind.api.util.DyeColor;
import com.voxelwind.api.game.item.util.ItemTypeUtil;
import com.voxelwind.api.game.level.block.data.Cake;
import com.voxelwind.api.game.level.block.data.Crops;
import com.voxelwind.api.server.Server;

import java.util.*;

/**
 * This class contains all block types recognized by Voxelwind and Pocket Edition.
 */
public class BlockTypes {
    private static Map<Integer, BlockType> BY_ID = new HashMap<>();
    private static final Random RANDOM = new Random();

    public static final BlockType AIR = new IntBlock(0, "air", 0, false, true, 0, 0, NothingDrop.INSTANCE);
    public static final BlockType STONE = new IntBlock(1, "stone", 64, true, false, 0, 15);
    public static final BlockType GRASS_BLOCK = new IntBlock(2, "grass_block", 64, true, false, 0, 15);
    public static final BlockType DIRT = new IntBlock(3, "dirt", 64, true, false, 0, 15);
    public static final BlockType COBBLESTONE = new IntBlock(4, "cobblestone", 64, true, false, 0, 15);
    public static final BlockType WOOD_PLANKS = new IntBlock(5, "wood_planks", 64, true, false, 0, 15);
    public static final BlockType SAPLING = new IntBlock(6, "sapling", 64, true, true, 0, 0);
    public static final BlockType BEDROCK = new IntBlock(7, "bedrock", 64, false, false, 0, 15);
    public static final BlockType WATER = new IntBlock(8, "water", 0, false, true, 0, 2, NothingDrop.INSTANCE);
    public static final BlockType WATER_STATIONARY = new IntBlock(9, "water", 0, false, true, 0, 2, NothingDrop.INSTANCE);
    public static final BlockType LAVA = new IntBlock(10, "lava", 0, false, true, 15, 0, NothingDrop.INSTANCE);
    public static final BlockType LAVA_STATIONAR = new IntBlock(11, "lava", 0, false, true, 15, 0, NothingDrop.INSTANCE);
    public static final BlockType SAND = new IntBlock(12, "sand", 64, true, false, 0, 15);
    public static final BlockType GRAVEL = new IntBlock(13, "gravel", 64, true, false, 0, 15);
    public static final BlockType GOLD_ORE = new IntBlock(14, "gold_ore", 64, true, false, 0, 15);
    public static final BlockType IRON_ORE = new IntBlock(15, "iron_ore", 64, true, false, 0, 15);
    public static final BlockType COAL_ORE = new IntBlock(16, "coal_ore", 64, true, false, 0, 15);
    public static final BlockType WOOD = new IntBlock(17, "wood", 64, true, false, 0, 15);
    public static final BlockType LEAVES = new IntBlock(18, "leaves", 64, true, true, 0, 0);
    public static final BlockType SPONGE = new IntBlock(19, "sponge", 64, true, false, 0, 15);
    public static final BlockType GLASS = new IntBlock(20, "glass", 64, true, true, 0, 0);
    public static final BlockType LAPIS_LAZULI_ORE = new IntBlock(21, "lapis_lazuli_ore", 64, true, false, 0, 15, (server, i2, i3) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.DYE)
                .itemData(Dyed.of(DyeColor.BLUE))
                .amount((4) + RANDOM.nextInt(5))
                .build());
    });
    public static final BlockType LAPIS_LAZULI_BLOCK = new IntBlock(22, "lapis_lazuli_block", 64, true, false, 0, 15);
    public static final BlockType DISPENSER = new IntBlock(23, "dispenser", 64, true, false, 0, 15);
    public static final BlockType SANDSTONE = new IntBlock(24, "sandstone", 64, true, false, 0, 15);
    public static final BlockType NOTE_BLOCK = new IntBlock(25, "note_block", 64, true, false, 0, 15);
    public static final BlockType BED = new IntBlock(26, "bed", 1, true, true, 0, 0, (server, i2, i3) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.BED)
                .amount(1)
                .build());
    });
    public static final BlockType POWERED_RAIL = new IntBlock(27, "powered_rail", 64, true, true, 0, 0);
    public static final BlockType DETECTOR_RAIL = new IntBlock(28, "detector_rail", 64, true, true, 0, 0);
    public static final BlockType COBWEB = new IntBlock(30, "cobweb", 64, true, true, 0, 0, (server, block, with) -> {
        if (with.getItemType() == ItemTypes.SHEARS || with.getItemType() == ItemTypes.IRON_SWORD || with.getItemType() == ItemTypes.GOLD_SWORD ||
                with.getItemType() == ItemTypes.DIAMOND_SWORD) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(with.getItemType() == ItemTypes.SHEARS ? block.getBlockState().getBlockType() : ItemTypes.STRING)
                    .amount(1)
                    .build());
        }
        return ImmutableList.of();
    });
    public static final BlockType TALL_GRASS = new IntBlock(31, "tall_grass", 64, true, true, 0, 0, (server, block, with) -> {
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
    });
    public static final BlockType DEAD_BUSH = new IntBlock(32, "dead_bush", 64, true, true, 0, 0, SelfForInDrop.SHEARS_ONLY);
    public static final BlockType WOOL = new IntBlock(35, "wool", 64, true, false, 0, 15, SelfDrop.INSTANCE, Dyed.class);
    public static final BlockType DANDELION = new IntBlock(37, "dandelion", 64, true, true, 0, 0);
    public static final BlockType FLOWER = new IntBlock(38, "flower", 64, true, true, 0, 0);
    public static final BlockType BROWN_MUSHROOM = new IntBlock(39, "brown_mushroom", 64, true, false, 1, 15);
    public static final BlockType RED_MUSHROOM = new IntBlock(40, "red_mushroom", 64, true, false, 1, 15);
    public static final BlockType BLOCK_OF_GOLD = new IntBlock(41, "block_of_gold", 64, true, false, 0, 15, SelfForInDrop.ALL_PICKAXES);
    public static final BlockType BLOCK_OF_IRON = new IntBlock(42, "block_of_iron", 64, true, false, 0, 15, SelfForInDrop.ALL_PICKAXES);
    public static final BlockType DOUBLE_STONE_SLAB = new IntBlock(43, "double_stone_slab", 64, true, true, 0, 0);
    public static final BlockType STONE_SLAB = new IntBlock(44, "stone_slab", 64, true, true, 0, 0);
    public static final BlockType BRICKS = new IntBlock(45, "bricks", 64, true, false, 0, 15);
    public static final BlockType TNT = new IntBlock(46, "tnt", 64, true, true, 0, 0);
    public static final BlockType BOOKSHELF = new IntBlock(47, "bookshelf", 64, true, false, 0, 15);
    public static final BlockType MOSS_STONE = new IntBlock(48, "moss_stone", 64, true, false, 0, 15);
    public static final BlockType OBSIDIAN = new IntBlock(49, "obsidian", 64, true, false, 0, 15);
    public static final BlockType TORCH = new IntBlock(50, "torch", 64, true, true, 0, 0);
    public static final BlockType FIRE = new IntBlock(51, "fire", 0, true, true, 15, 0);
    public static final BlockType MONSTER_SPAWNER = new IntBlock(52, "monster_spawner", 64, true, true, 0, 0);
    public static final BlockType OAK_WOOD_STAIRS = new IntBlock(53, "oak_wood_stairs", 64, true, true, 0, 15);
    public static final BlockType CHEST = new IntBlock(54, "chest", 64, true, true, 0, 0);
    public static final BlockType REDSTONE_WIRE = new IntBlock(55, "redstone_wire", 64, true, true, 0, 0);
    public static final BlockType DIAMOND_ORE = new IntBlock(56, "diamond_ore", 64, true, false, 0, 15);
    public static final BlockType BLOCK_OF_DIAMOND = new IntBlock(57, "block_of_diamond", 64, true, false, 0, 15);
    public static final BlockType CRAFTING_TABLE = new IntBlock(58, "crafting_table", 64, true, false, 0, 15);
    public static final BlockType CROPS = new IntBlock(59, "crops", 0, true, false, 0, 15, (server, block, with) -> {
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
    });
    public static final BlockType FARMLAND = new IntBlock(60, "farmland", 64, true, true, 0, 15);
    public static final BlockType FURNACE = new IntBlock(61, "furnace", 64, true, true, 13, 0, SelfForInDrop.ALL_PICKAXES);
    public static final BlockType FURNACE_ACTIVE = new IntBlock(62, "furnace", 64, true, true, 13, 0, SelfForInDrop.ALL_PICKAXES);
    public static final BlockType SIGN = new IntBlock(63, "sign", 16, true, true, 0, 0);
    public static final BlockType WOODEN_DOOR = new IntBlock(64, "wooden_door", 64, true, true, 0, 0);
    public static final BlockType LADDER = new IntBlock(65, "ladder", 64, true, true, 0, 0);
    public static final BlockType RAIL = new IntBlock(66, "rail", 64, true, true, 0, 0);
    public static final BlockType COBBLESTONE_STAIRS = new IntBlock(67, "cobblestone_stairs", 64, true, true, 0, 15);
    public static final BlockType WALL_SIGN = new IntBlock(68, "sign", 16, true, true, 0, 0);
    public static final BlockType LEVER = new IntBlock(69, "lever", 64, true, true, 0, 0);
    public static final BlockType STONE_PRESSURE_PLATE = new IntBlock(70, "stone_pressure_plate", 64, true, true, 0, 0);
    public static final BlockType IRON_DOOR = new IntBlock(71, "iron_door", 64, true, true, 0, 0);
    public static final BlockType WOODEN_PRESSURE_PLATE = new IntBlock(72, "wooden_pressure_plate", 64, true, true, 0, 0);
    public static final BlockType REDSTONE_ORE = new IntBlock(73, "redstone_ore", 64, true, true, 9, 0, (server, i2, i3) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.REDSTONE)
                .amount(4 + RANDOM.nextInt(2))
                .build());
    }, null);
    public static final BlockType REDSTONE_ORE_ACTIVE = new IntBlock(74, "redstone_ore", 64, true, true, 9, 0, (server, i2, i3) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.REDSTONE)
                .amount(4 + RANDOM.nextInt(2))
                .build());
    }, null);
    public static final BlockType REDSTONE_TORCH = new IntBlock(75, "redstone_torch", 64, true, true, 7, 0);
    public static final BlockType REDSTONE_TORCH_ACTIVE = new IntBlock(76, "redstone_torch", 64, true, true, 7, 0, (server, i2, i3) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(REDSTONE_TORCH)
                .amount(1)
                .build());
    }, null);
    public static final BlockType STONE_BUTTON = new IntBlock(77, "stone_button", 64, true, true, 0, 0);
    public static final BlockType TOP_SNOW = new IntBlock(78, "top_snow", 64, true, true, 0, 0, (server, block, itemStack) -> {
        if (ItemTypeUtil.isShovel(itemStack.getItemType()) && RANDOM.nextBoolean()) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.SNOWBALL)
                    .amount(1)
                    .build());
        }
        return ImmutableList.of();
    }, null);
    public static final BlockType ICE = new IntBlock(79, "ice", 64, true, true, 0, 0);
    public static final BlockType SNOW = new IntBlock(80, "snow", 64, true, false, 0, 15);
    public static final BlockType CACTUS = new IntBlock(81, "cactus", 64, true, true, 0, 0);
    public static final BlockType CLAY = new IntBlock(82, "clay", 64, true, false, 0, 15);
    public static final BlockType SUGAR_CANE = new IntBlock(83, "sugar_cane", 64, true, true, 0, 0);
    public static final BlockType FENCE = new IntBlock(85, "fence", 64, true, true, 0, 0);
    public static final BlockType PUMPKIN = new IntBlock(86, "pumpkin", 64, true, true, 0, 15);
    public static final BlockType NETHERRACK = new IntBlock(87, "netherrack", 64, true, false, 0, 15);
    public static final BlockType SOUL_SAND = new IntBlock(88, "soul_sand", 64, true, false, 0, 15);
    public static final BlockType GLOWSTONE = new IntBlock(89, "glowstone", 64, true, true, 0, 0);
    public static final BlockType PORTAL = new IntBlock(90, "portal", 0, false, false, 0, 15, NothingDrop.INSTANCE);
    public static final BlockType JACK_OLANTERN = new IntBlock(91, "jack_o'lantern", 64, true, true, 15, 15);
    public static final BlockType CAKE = new IntBlock(92, "cake", 1, true, true, 0, 0, NothingDrop.INSTANCE, Cake.class);
    public static final BlockType REDSTONE_REPEATER = new IntBlock(93, "redstone_repeater", 64, true, true, 0, 0);
    public static final BlockType REDSTONE_REPEATER_ACTIVE = new IntBlock(94, "redstone_repeater", 64, true, true, 0, 0);
    public static final BlockType INVISIBLE_BEDROCK = new IntBlock(95, "invisible_bedrock", 64, false, true, 0, 0);
    public static final BlockType TRAPDOOR = new IntBlock(96, "trapdoor", 64, true, true, 0, 0);
    public static final BlockType MONSTER_EGG = new IntBlock(97, "monster_egg", 64, true, false, 0, 15);
    public static final BlockType STONE_BRICK = new IntBlock(98, "stone_brick", 64, true, false, 0, 15);
    public static final BlockType BROWN_MUSHROOM_BLOCK = new IntBlock(99, "brown_mushroom", 64, true, false, 0, 15);
    public static final BlockType RED_MUSHROOM_BLOCK = new IntBlock(100, "red_mushroom", 64, true, false, 0, 15);
    public static final BlockType IRON_BARS = new IntBlock(101, "iron_bars", 64, true, true, 0, 0);
    public static final BlockType GLASS_PANE = new IntBlock(102, "glass_pane", 64, true, true, 0, 0);
    public static final BlockType MELON = new IntBlock(103, "melon", 64, true, true, 0, 15);
    public static final BlockType PUMPKIN_STEM = new IntBlock(104, "pumpkin_stem", 64, true, true, 0, 0);
    public static final BlockType MELON_STEM = new IntBlock(105, "melon_stem", 64, true, true, 0, 0);
    public static final BlockType VINES = new IntBlock(106, "vines", 64, true, true, 0, 0);
    public static final BlockType FENCE_GATE = new IntBlock(107, "fence_gate", 64, true, true, 0, 0);
    public static final BlockType BRICK_STAIRS = new IntBlock(108, "brick_stairs", 64, true, true, 0, 15);
    public static final BlockType STONE_BRICK_STAIRS = new IntBlock(109, "stone_brick_stairs", 64, true, true, 0, 15);
    public static final BlockType MYCELIUM = new IntBlock(110, "mycelium", 64, true, false, 0, 15);
    public static final BlockType LILY_PAD = new IntBlock(111, "lily_pad", 64, true, true, 0, 0);
    public static final BlockType NETHER_BRICK = new IntBlock(112, "nether_brick", 64, true, false, 0, 15);
    public static final BlockType NETHER_BRICK_FENCE = new IntBlock(113, "nether_brick_fence", 64, true, true, 0, 0);
    public static final BlockType NETHER_BRICK_STAIRS = new IntBlock(114, "nether_brick_stairs", 64, true, true, 0, 15);
    public static final BlockType NETHER_WART = new IntBlock(115, "nether_wart", 64, true, true, 0, 0);
    public static final BlockType ENCHANTMENT_TABLE = new IntBlock(116, "enchantment_table", 64, true, true, 0, 0);
    public static final BlockType BREWING_STAND = new IntBlock(117, "brewing_stand", 64, true, true, 1, 0);
    public static final BlockType CAULDRON = new IntBlock(118, "cauldron", 64, true, true, 0, 0);
    public static final BlockType END_PORTAL_FRAME = new IntBlock(120, "end_portal_frame", 64, false, true, 1, 0);
    public static final BlockType END_STONE = new IntBlock(121, "end_stone", 64, true, false, 0, 15);
    public static final BlockType REDSTONE_LAMP = new IntBlock(122, "redstone_lamp", 64, true, true, 15, 0);
    public static final BlockType REDSTONE_LAMP_ACTIVE = new IntBlock(123, "redstone_lamp", 64, true, true, 15, 0);
    public static final BlockType ACTIVATOR_RAIL = new IntBlock(126, "activator_rail", 64, true, true, 0, 0);
    public static final BlockType COCOA = new IntBlock(127, "cocoa", 64, true, true, 0, 0, (server, block, with) -> {
        // TODO: fix this once proper data type is implemented
        BlockData data = block.getBlockState().getBlockData();
        int amt = 1;
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.DYE)
                .itemData(Dyed.of(DyeColor.BROWN))
                .amount(amt)
                .build());
    });
    public static final BlockType SANDSTONE_STAIRS = new IntBlock(128, "sandstone_stairs", 64, true, true, 0, 15);
    public static final BlockType EMERALD_ORE = new IntBlock(129, "emerald_ore", 64, true, false, 0, 15);
    public static final BlockType TRIPWIRE_HOOK = new IntBlock(131, "tripwire_hook", 64, true, true, 0, 0);
    public static final BlockType TRIPWIRE = new IntBlock(132, "tripwire", 64, true, true, 0, 0);
    public static final BlockType BLOCK_OF_EMERALD = new IntBlock(133, "block_of_emerald", 64, true, false, 0, 15);
    public static final BlockType SPRUCE_WOOD_STAIRS = new IntBlock(134, "spruce_wood_stairs", 64, true, true, 0, 15);
    public static final BlockType BIRCH_WOOD_STAIRS = new IntBlock(135, "birch_wood_stairs", 64, true, true, 0, 15);
    public static final BlockType JUNGLE_WOOD_STAIRS = new IntBlock(136, "jungle_wood_stairs", 64, true, true, 0, 15);
    public static final BlockType COBBLESTONE_WALL = new IntBlock(139, "cobblestone_wall", 64, true, true, 0, 0);
    public static final BlockType FLOWER_POT = new IntBlock(140, "flower_pot", 64, true, true, 0, 0);
    public static final BlockType CARROTS = new IntBlock(141, "carrots", 64, true, false, 0, 15);
    public static final BlockType POTATO = new IntBlock(142, "potato", 64, true, false, 0, 15);
    public static final BlockType WOODEN_BUTTON = new IntBlock(143, "wooden_button", 64, true, true, 0, 0);
    public static final BlockType MOB_HEAD = new IntBlock(144, "mob_head", 64, true, true, 0, 0);
    public static final BlockType ANVIL = new IntBlock(145, "anvil", 64, true, true, 0, 0);
    public static final BlockType TRAPPED_CHEST = new IntBlock(146, "trapped_chest", 64, true, true, 0, 0);
    public static final BlockType WEIGHTED_PRESSURE_PLATE_LIGHT = new IntBlock(147, "weighted_pressure_plate", 64, true, true, 0, 0);
    public static final BlockType WEIGHTED_PRESSURE_PLATE_HEAVY = new IntBlock(148, "weighted_pressure_plate", 64, true, true, 0, 0);
    public static final BlockType DAYLIGHT_SENSOR = new IntBlock(151, "daylight_sensor", 64, true, true, 0, 0);
    public static final BlockType BLOCK_OF_REDSTONE = new IntBlock(152, "block_of_redstone", 64, true, true, 0, 0);
    public static final BlockType NETHER_QUARTZ_ORE = new IntBlock(153, "nether_quartz_ore", 64, true, false, 0, 15, SelfForInDrop.ALL_PICKAXES);
    public static final BlockType BLOCK_OF_QUARTZ = new IntBlock(155, "block_of_quartz", 64, true, false, 0, 15);
    public static final BlockType QUARTZ_STAIRS = new IntBlock(156, "quartz_stairs", 64, true, true, 0, 15);
    public static final BlockType WOODEN_DOUBLE_SLAB = new IntBlock(157, "wooden_double_slab", 64, true, true, 0, 0);
    public static final BlockType WOODEN_SLAB = new IntBlock(158, "wooden_slab", 64, true, true, 0, 0);
    public static final BlockType STAINED_CLAY = new IntBlock(159, "stained_clay", 64, true, false, 0, 15, SelfDrop.INSTANCE, Dyed.class, Dyed::of);
    public static final BlockType ACACIA_LEAVES = new IntBlock(161, "acacia_leaves", 64, true, true, 0, 0);
    public static final BlockType ACACIA_WOOD = new IntBlock(162, "acacia_wood", 64, true, false, 0, 15);
    public static final BlockType ACACIA_WOOD_STAIRS = new IntBlock(163, "acacia_wood_stairs", 64, true, true, 0, 15);
    public static final BlockType DARK_OAK_WOOD_STAIRS = new IntBlock(164, "dark_oak_wood_stairs", 64, true, true, 0, 15);
    public static final BlockType IRON_TRAPDOOR = new IntBlock(167, "iron_trapdoor", 64, true, true, 0, 0);
    public static final BlockType HAY_BALE = new IntBlock(170, "hay_bale", 64, true, false, 0, 15);
    public static final BlockType CARPET = new IntBlock(171, "carpet", 64, true, true, 0, 0, SelfDrop.INSTANCE, Dyed.class, Dyed::of);
    public static final BlockType HARDENED_CLAY = new IntBlock(172, "hardened_clay", 64, true, false, 0, 15);
    public static final BlockType BLOCK_OF_COAL = new IntBlock(173, "block_of_coal", 64, true, false, 0, 15);
    public static final BlockType PACKED_ICE = new IntBlock(174, "packed_ice", 64, true, false, 0, 15);
    public static final BlockType SUNFLOWER = new IntBlock(175, "sunflower", 64, true, true, 0, 0);
    public static final BlockType INVERTED_DAYLIGHT_SENSOR = new IntBlock(178, "inverted_daylight_sensor", 64, true, true, 0, 0);
    public static final BlockType SPRUCE_FENCE_GATE = new IntBlock(183, "spruce_fence_gate", 64, true, true, 0, 0);
    public static final BlockType BIRCH_FENCE_GATE = new IntBlock(184, "birch_fence_gate", 64, true, true, 0, 0);
    public static final BlockType JUNGLE_FENCE_GATE = new IntBlock(185, "jungle_fence_gate", 64, true, true, 0, 0);
    public static final BlockType DARK_OAK_FENCE_GATE = new IntBlock(186, "dark_oak_fence_gate", 64, true, true, 0, 0);
    public static final BlockType ACACIA_FENCE_GATE = new IntBlock(187, "acacia_fence_gate", 64, true, true, 0, 0);
    public static final BlockType GRASS_PATH = new IntBlock(198, "grass_path", 64, true, true, 0, 0);
    public static final BlockType ITEM_FRAME = new IntBlock(199, "item_frame", 64, true, false, 0, 15);
    public static final BlockType PODZOL = new IntBlock(243, "podzol", 64, true, false, 0, 15);
    public static final BlockType BEETROOT = new IntBlock(244, "beetroot", 64, true, true, 0, 0);
    public static final BlockType STONECUTTER = new IntBlock(245, "stonecutter", 64, true, false, 0, 15);
    public static final BlockType GLOWING_OBSIDIAN = new IntBlock(246, "glowing_obsidian", 64, true, false, 12, 15);
    public static final BlockType NETHER_REACTOR_CORE = new IntBlock(247, "nether_reactor_core", 64, true, false, 0, 15);
    public static final BlockType OBSERVER = new IntBlock(251, "observer", 64, true, false, 0, 15, SelfForInDrop.ALL_PICKAXES);

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

    private static class IntBlock implements BlockType {
        private final int id;
        private final String name;
        private final int maxStackSize;
        private final boolean diggable;
        private final boolean transparent;
        private final int emitLight;
        private final int filterLight;
        private final DroppedHandler dropHandler;
        private final Class<? extends BlockData> aClass;
        private final ItemTypes.FromMetadata fromMetadata;

        public IntBlock(int id, String name, int maxStackSize, boolean diggable, boolean transparent, int emitLight, int filterLight) {
            this(id, name, maxStackSize, diggable, transparent, emitLight, filterLight, SelfDrop.INSTANCE);
        }

        public IntBlock(int id, String name, int maxStackSize, boolean diggable, boolean transparent, int emitLight, int filterLight, DroppedHandler handler) {
            this(id, name, maxStackSize, diggable, transparent, emitLight, filterLight, handler, null);
        }

        public IntBlock(int id, String name, int maxStackSize, boolean diggable, boolean transparent, int emitLight, int filterLight, DroppedHandler dropHandler, Class<? extends BlockData> aClass) {
            this(id, name, maxStackSize, diggable, transparent, emitLight, filterLight, SelfDrop.INSTANCE, aClass, null);
        }

        public IntBlock(int id, String name, int maxStackSize, boolean diggable, boolean transparent, int emitLight, int filterLight, DroppedHandler dropHandler, Class<? extends BlockData> aClass, ItemTypes.FromMetadata fromMetadata) {
            this.id = id;
            this.name = name;
            this.maxStackSize = maxStackSize;
            this.diggable = diggable;
            this.transparent = transparent;
            this.emitLight = emitLight;
            this.filterLight = filterLight;
            this.dropHandler = dropHandler;
            this.aClass = aClass;
            this.fromMetadata = fromMetadata;

            BY_ID.put(id, this);
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Class<? extends ItemData> getMaterialDataClass() {
            if (ItemData.class.isAssignableFrom(aClass)) {
                return (Class<? extends ItemData>) aClass;
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
            return aClass;
        }
    }
}
