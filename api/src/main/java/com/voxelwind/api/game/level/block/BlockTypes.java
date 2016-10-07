package com.voxelwind.api.game.level.block;

import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.data.Dyed;
import com.voxelwind.api.game.item.data.wood.Log;
import com.voxelwind.api.game.item.data.wood.Wood;
import com.voxelwind.api.game.level.block.data.Cake;
import com.voxelwind.api.game.level.block.data.Crops;
import com.voxelwind.api.game.level.block.data.TopSnow;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.api.game.level.blockentities.FlowerpotBlockEntity;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Builder;
import lombok.experimental.UtilityClass;

/**
 * This class contains all block types recognized by Voxelwind and Pocket Edition.
 */
@UtilityClass
public class BlockTypes {
    private static TIntObjectMap<BlockType> BY_ID = new TIntObjectHashMap<>(192);

    public static final BlockType AIR = IntBlock.builder().name("air").id(0).maxStackSize(0).diggable(false).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType STONE = IntBlock.builder().name("stone").id(1).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType GRASS_BLOCK = IntBlock.builder().name("grass_block").id(2).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType DIRT = IntBlock.builder().name("dirt").id(3).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType COBBLESTONE = IntBlock.builder().name("cobblestone").id(4).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType WOOD_PLANKS = IntBlock.builder().name("wood_planks").id(5).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).metadataClass(Wood.class).build();
    public static final BlockType SAPLING = IntBlock.builder().name("sapling").id(6).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType BEDROCK = IntBlock.builder().name("bedrock").id(7).maxStackSize(64).diggable(false).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType WATER = IntBlock.builder().name("water").id(8).maxStackSize(0).diggable(false).transparent(true).emitLight(0).filterLight(2).build();
    public static final BlockType STATIONARY_WATER = IntBlock.builder().name("stationary_water").id(9).maxStackSize(0).diggable(false).transparent(true).emitLight(0).filterLight(2).build();
    public static final BlockType LAVA = IntBlock.builder().name("lava").id(10).maxStackSize(0).diggable(false).transparent(true).emitLight(15).filterLight(0).build();
    public static final BlockType STATIONARY_LAVA = IntBlock.builder().name("stationary_lava").id(11).maxStackSize(0).diggable(false).transparent(true).emitLight(15).filterLight(0).build();
    public static final BlockType SAND = IntBlock.builder().name("sand").id(12).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType GRAVEL = IntBlock.builder().name("gravel").id(13).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType GOLD_ORE = IntBlock.builder().name("gold_ore").id(14).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType IRON_ORE = IntBlock.builder().name("iron_ore").id(15).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType COAL_ORE = IntBlock.builder().name("coal_ore").id(16).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType WOOD = IntBlock.builder().name("wood").id(17).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).metadataClass(Log.class).build();
    public static final BlockType LEAVES = IntBlock.builder().name("leaves").id(18).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType SPONGE = IntBlock.builder().name("sponge").id(19).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType GLASS = IntBlock.builder().name("glass").id(20).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType LAPIS_LAZULI_ORE = IntBlock.builder().name("lapis_lazuli_ore").id(21).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType LAPIS_LAZULI_BLOCK = IntBlock.builder().name("lapis_lazuli_block").id(22).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType DISPENSER = IntBlock.builder().name("dispenser").id(23).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType SANDSTONE = IntBlock.builder().name("sandstone").id(24).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType NOTE_BLOCK = IntBlock.builder().name("note_block").id(25).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType BED = IntBlock.builder().name("bed").id(26).maxStackSize(1).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType POWERED_RAIL = IntBlock.builder().name("powered_rail").id(27).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType DETECTOR_RAIL = IntBlock.builder().name("detector_rail").id(28).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType STICKY_PISTON = IntBlock.builder().name("sticky_piston").id(29).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType COBWEB = IntBlock.builder().name("cobweb").id(30).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType TALL_GRASS = IntBlock.builder().name("tall_grass").id(31).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType DEAD_BUSH = IntBlock.builder().name("dead_bush").id(32).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType PISTON = IntBlock.builder().name("piston").id(33).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType PISTON_HEAD = IntBlock.builder().name("piston_head").id(34).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType WOOL = IntBlock.builder().name("wool").id(35).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).metadataClass(Dyed.class).build();
    public static final BlockType DANDELION = IntBlock.builder().name("dandelion").id(37).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType FLOWER = IntBlock.builder().name("flower").id(38).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType BROWN_MUSHROOM = IntBlock.builder().name("brown_mushroom").id(39).maxStackSize(64).diggable(true).transparent(false).emitLight(1).filterLight(15).build();
    public static final BlockType RED_MUSHROOM = IntBlock.builder().name("red_mushroom").id(40).maxStackSize(64).diggable(true).transparent(false).emitLight(1).filterLight(15).build();
    public static final BlockType GOLD_BLOCK = IntBlock.builder().name("gold_block").id(41).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType IRON_BLOCK = IntBlock.builder().name("iron_block").id(42).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType DOUBLE_STONE_SLAB = IntBlock.builder().name("double_stone_slab").id(43).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType STONE_SLAB = IntBlock.builder().name("stone_slab").id(44).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType BRICKS = IntBlock.builder().name("bricks").id(45).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType TNT = IntBlock.builder().name("tnt").id(46).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType BOOKSHELF = IntBlock.builder().name("bookshelf").id(47).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType MOSS_STONE = IntBlock.builder().name("moss_stone").id(48).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType OBSIDIAN = IntBlock.builder().name("obsidian").id(49).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType TORCH = IntBlock.builder().name("torch").id(50).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType FIRE = IntBlock.builder().name("fire").id(51).maxStackSize(0).diggable(true).transparent(true).emitLight(15).filterLight(0).build();
    public static final BlockType MONSTER_SPAWNER = IntBlock.builder().name("monster_spawner").id(52).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType OAK_WOOD_STAIRS = IntBlock.builder().name("oak_wood_stairs").id(53).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType CHEST = IntBlock.builder().name("chest").id(54).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType REDSTONE_WIRE = IntBlock.builder().name("redstone_wire").id(55).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType DIAMOND_ORE = IntBlock.builder().name("diamond_ore").id(56).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType DIAMOND_BLOCK = IntBlock.builder().name("diamond_block").id(57).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType CRAFTING_TABLE = IntBlock.builder().name("crafting_table").id(58).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType CROPS = IntBlock.builder().name("crops").id(59).maxStackSize(0).diggable(true).transparent(false).emitLight(0).filterLight(15).metadataClass(Crops.class).build();
    public static final BlockType FARMLAND = IntBlock.builder().name("farmland").id(60).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType FURNACE = IntBlock.builder().name("furnace").id(61).maxStackSize(64).diggable(true).transparent(true).emitLight(13).filterLight(0).build();
    public static final BlockType BURNING_FURNACE = IntBlock.builder().name("burning_furnace").id(62).maxStackSize(64).diggable(true).transparent(true).emitLight(13).filterLight(0).build();
    public static final BlockType SIGN = IntBlock.builder().name("sign").id(63).maxStackSize(16).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType WOODEN_DOOR = IntBlock.builder().name("wooden_door").id(64).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType LADDER = IntBlock.builder().name("ladder").id(65).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType RAIL = IntBlock.builder().name("rail").id(66).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType COBBLESTONE_STAIRS = IntBlock.builder().name("cobblestone_stairs").id(67).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType WALL_SIGN = IntBlock.builder().name("wall_sign").id(68).maxStackSize(16).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType LEVER = IntBlock.builder().name("lever").id(69).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType STONE_PRESSURE_PLATE = IntBlock.builder().name("stone_pressure_plate").id(70).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType IRON_DOOR = IntBlock.builder().name("iron_door").id(71).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType WOODEN_PRESSURE_PLATE = IntBlock.builder().name("wooden_pressure_plate").id(72).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType REDSTONE_ORE = IntBlock.builder().name("redstone_ore").id(73).maxStackSize(64).diggable(true).transparent(true).emitLight(9).filterLight(0).build();
    public static final BlockType GLOWING_REDSTONE_ORE = IntBlock.builder().name("glowing_redstone_ore").id(74).maxStackSize(64).diggable(true).transparent(true).emitLight(9).filterLight(0).build();
    public static final BlockType REDSTONE_TORCH = IntBlock.builder().name("redstone_torch").id(75).maxStackSize(64).diggable(true).transparent(true).emitLight(7).filterLight(0).build();
    public static final BlockType REDSTONE_TORCH_ACTIVE = IntBlock.builder().name("redstone_torch_active").id(76).maxStackSize(64).diggable(true).transparent(true).emitLight(7).filterLight(0).build();
    public static final BlockType STONE_BUTTON = IntBlock.builder().name("stone_button").id(77).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType TOP_SNOW = IntBlock.builder().name("top_snow").id(78).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).metadataClass(TopSnow.class).build();
    public static final BlockType ICE = IntBlock.builder().name("ice").id(79).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType SNOW = IntBlock.builder().name("snow").id(80).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType CACTUS = IntBlock.builder().name("cactus").id(81).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType CLAY = IntBlock.builder().name("clay").id(82).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType SUGAR_CANE = IntBlock.builder().name("sugar_cane").id(83).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType FENCE = IntBlock.builder().name("fence").id(85).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType PUMPKIN = IntBlock.builder().name("pumpkin").id(86).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType NETHERRACK = IntBlock.builder().name("netherrack").id(87).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType SOUL_SAND = IntBlock.builder().name("soul_sand").id(88).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType GLOWSTONE = IntBlock.builder().name("glowstone").id(89).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType PORTAL = IntBlock.builder().name("portal").id(90).maxStackSize(0).diggable(false).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType JACK_OLANTERN = IntBlock.builder().name("jack_olantern").id(91).maxStackSize(64).diggable(true).transparent(true).emitLight(15).filterLight(15).build();
    public static final BlockType CAKE = IntBlock.builder().name("cake").id(92).maxStackSize(1).diggable(true).transparent(true).emitLight(0).filterLight(0).metadataClass(Cake.class).build();
    public static final BlockType REDSTONE_REPEATER = IntBlock.builder().name("redstone_repeater").id(93).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType REDSTONE_REPEATER_ACTIVE = IntBlock.builder().name("redstone_repeater_active").id(94).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType INVISIBLE_BEDROCK = IntBlock.builder().name("invisible_bedrock").id(95).maxStackSize(64).diggable(false).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType TRAPDOOR = IntBlock.builder().name("trapdoor").id(96).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType MONSTER_EGG = IntBlock.builder().name("monster_egg").id(97).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType STONE_BRICK = IntBlock.builder().name("stone_brick").id(98).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType BROWN_MUSHROOM_BLOCK = IntBlock.builder().name("brown_mushroom_block").id(99).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType RED_MUSHROOM_BLOCK = IntBlock.builder().name("red_mushroom_block").id(100).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType IRON_BARS = IntBlock.builder().name("iron_bars").id(101).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType GLASS_PANE = IntBlock.builder().name("glass_pane").id(102).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType MELON = IntBlock.builder().name("melon").id(103).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType PUMPKIN_STEM = IntBlock.builder().name("pumpkin_stem").id(104).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).metadataClass(Crops.class).build();
    public static final BlockType MELON_STEM = IntBlock.builder().name("melon_stem").id(105).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).metadataClass(Crops.class).build();
    public static final BlockType VINES = IntBlock.builder().name("vines").id(106).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType FENCE_GATE = IntBlock.builder().name("fence_gate").id(107).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType BRICK_STAIRS = IntBlock.builder().name("brick_stairs").id(108).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType STONE_BRICK_STAIRS = IntBlock.builder().name("stone_brick_stairs").id(109).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType MYCELIUM = IntBlock.builder().name("mycelium").id(110).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType LILY_PAD = IntBlock.builder().name("lily_pad").id(111).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType NETHER_BRICK = IntBlock.builder().name("nether_brick").id(112).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType NETHER_BRICK_FENCE = IntBlock.builder().name("nether_brick_fence").id(113).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType NETHER_BRICK_STAIRS = IntBlock.builder().name("nether_brick_stairs").id(114).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType NETHER_WART = IntBlock.builder().name("nether_wart").id(115).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType ENCHANTMENT_TABLE = IntBlock.builder().name("enchantment_table").id(116).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType BREWING_STAND = IntBlock.builder().name("brewing_stand").id(117).maxStackSize(64).diggable(true).transparent(true).emitLight(1).filterLight(0).build();
    public static final BlockType CAULDRON = IntBlock.builder().name("cauldron").id(118).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType END_PORTAL_FRAME = IntBlock.builder().name("end_portal_frame").id(120).maxStackSize(64).diggable(false).transparent(true).emitLight(1).filterLight(0).build();
    public static final BlockType END_STONE = IntBlock.builder().name("end_stone").id(121).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType REDSTONE_LAMP = IntBlock.builder().name("redstone_lamp").id(123).maxStackSize(64).diggable(true).transparent(true).emitLight(15).filterLight(0).build();
    public static final BlockType REDSTONE_LAMP_ACTIVE = IntBlock.builder().name("redstone_lamp_active").id(124).maxStackSize(64).diggable(true).transparent(true).emitLight(15).filterLight(0).build();
    public static final BlockType DROPPER = IntBlock.builder().name("dropper").id(125).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType ACTIVATOR_RAIL = IntBlock.builder().name("activator_rail").id(126).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType COCOA = IntBlock.builder().name("cocoa").id(127).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType SANDSTONE_STAIRS = IntBlock.builder().name("sandstone_stairs").id(128).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType EMERALD_ORE = IntBlock.builder().name("emerald_ore").id(129).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType TRIPWIRE_HOOK = IntBlock.builder().name("tripwire_hook").id(131).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType TRIPWIRE = IntBlock.builder().name("tripwire").id(132).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType EMERALD_BLOCK = IntBlock.builder().name("emerald_block").id(133).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType SPRUCE_WOOD_STAIRS = IntBlock.builder().name("spruce_wood_stairs").id(134).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType BIRCH_WOOD_STAIRS = IntBlock.builder().name("birch_wood_stairs").id(135).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType JUNGLE_WOOD_STAIRS = IntBlock.builder().name("jungle_wood_stairs").id(136).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType COBBLESTONE_WALL = IntBlock.builder().name("cobblestone_wall").id(139).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType FLOWER_POT = IntBlock.builder().name("flower_pot").id(140).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).metadataClass(FlowerpotBlockEntity.class).build();
    public static final BlockType CARROTS = IntBlock.builder().name("carrots").id(141).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).metadataClass(Crops.class).build();
    public static final BlockType POTATO = IntBlock.builder().name("potato").id(142).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).metadataClass(Crops.class).build();
    public static final BlockType WOODEN_BUTTON = IntBlock.builder().name("wooden_button").id(143).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType MOB_HEAD = IntBlock.builder().name("mob_head").id(144).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType ANVIL = IntBlock.builder().name("anvil").id(145).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType TRAPPED_CHEST = IntBlock.builder().name("trapped_chest").id(146).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType WEIGHTED_PRESSURE_PLATE_LIGHT = IntBlock.builder().name("weighted_pressure_plate_light").id(147).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType WEIGHTED_PRESSURE_PLATE_HEAVY = IntBlock.builder().name("weighted_pressure_plate_heavy").id(148).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType REDSTONE_COMPARATOR_UNPOWERED = IntBlock.builder().name("redstone_comparator_unpowered").id(149).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType REDSTONE_COMPARATOR_POWERED = IntBlock.builder().name("redstone_comparator_powered").id(150).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType DAYLIGHT_SENSOR = IntBlock.builder().name("daylight_sensor").id(151).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType REDSTONE_BLOCK = IntBlock.builder().name("redstone_block").id(152).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType NETHER_QUARTZ_ORE = IntBlock.builder().name("nether_quartz_ore").id(153).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType HOPPER = IntBlock.builder().name("hopper").id(154).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType QUARTZ_BLOCK = IntBlock.builder().name("quartz_block").id(155).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType QUARTZ_STAIRS = IntBlock.builder().name("quartz_stairs").id(156).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType WOODEN_DOUBLE_SLAB = IntBlock.builder().name("wooden_double_slab").id(157).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType WOODEN_SLAB = IntBlock.builder().name("wooden_slab").id(158).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType STAINED_CLAY = IntBlock.builder().name("stained_clay").id(159).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).metadataClass(Dyed.class).build();
    public static final BlockType ACACIA_LEAVES = IntBlock.builder().name("acacia_leaves").id(161).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType ACACIA_WOOD = IntBlock.builder().name("acacia_wood").id(162).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).metadataClass(Log.class).build();
    public static final BlockType ACACIA_WOOD_STAIRS = IntBlock.builder().name("acacia_wood_stairs").id(163).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType DARK_OAK_WOOD_STAIRS = IntBlock.builder().name("dark_oak_wood_stairs").id(164).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType SLIME_BLOCK = IntBlock.builder().name("slime_block").id(165).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType IRON_TRAPDOOR = IntBlock.builder().name("iron_trapdoor").id(167).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType HAY_BALE = IntBlock.builder().name("hay_bale").id(170).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType CARPET = IntBlock.builder().name("carpet").id(171).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType HARDENED_CLAY = IntBlock.builder().name("hardened_clay").id(172).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType COAL_BLOCK = IntBlock.builder().name("coal_block").id(173).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType PACKED_ICE = IntBlock.builder().name("packed_ice").id(174).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType SUNFLOWER = IntBlock.builder().name("sunflower").id(175).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType INVERTED_DAYLIGHT_SENSOR = IntBlock.builder().name("inverted_daylight_sensor").id(178).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType RED_SANDSTONE = IntBlock.builder().name("red_sandstone").id(179).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType RED_SANDSTONE_STAIRS = IntBlock.builder().name("red_sandstone_stairs").id(180).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).build();
    public static final BlockType DOUBLE_RED_SANDSTONE_SLAB = IntBlock.builder().name("double_red_sandstone_slab").id(181).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType RED_SANDSTONE_SLAB = IntBlock.builder().name("red_sandstone_slab").id(182).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType SPRUCE_FENCE_GATE = IntBlock.builder().name("spruce_fence_gate").id(183).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType BIRCH_FENCE_GATE = IntBlock.builder().name("birch_fence_gate").id(184).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType JUNGLE_FENCE_GATE = IntBlock.builder().name("jungle_fence_gate").id(185).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType DARK_OAK_FENCE_GATE = IntBlock.builder().name("dark_oak_fence_gate").id(186).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType ACACIA_FENCE_GATE = IntBlock.builder().name("acacia_fence_gate").id(187).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType SPRUCE_DOOR = IntBlock.builder().name("spruce_door").id(193).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType BIRCH_DOOR = IntBlock.builder().name("birch_door").id(194).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType JUNGLE_DOOR = IntBlock.builder().name("jungle_door").id(195).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType ACACIA_DOOR = IntBlock.builder().name("acacia_door").id(196).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType DARK_OAK_DOOR = IntBlock.builder().name("dark_oak_door").id(197).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType GRASS_PATH = IntBlock.builder().name("grass_path").id(198).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType ITEM_FRAME = IntBlock.builder().name("item_frame").id(199).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType PODZOL = IntBlock.builder().name("podzol").id(243).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType BEETROOT = IntBlock.builder().name("beetroot").id(244).maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).build();
    public static final BlockType STONECUTTER = IntBlock.builder().name("stonecutter").id(245).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();
    public static final BlockType GLOWING_OBSIDIAN = IntBlock.builder().name("glowing_obsidian").id(246).maxStackSize(64).diggable(true).transparent(false).emitLight(12).filterLight(15).build();
    public static final BlockType NETHER_REACTOR_CORE = IntBlock.builder().name("nether_reactor_core").id(247).maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).build();

    public static BlockType forId(int data) {
        BlockType type = BY_ID.get(data);
        if (type == null) {
            throw new IllegalArgumentException("ID " + data + " is not valid.");
        }
        return type;
    }

    @Builder
    private static class IntBlock implements BlockType {
        private final int id;
        private final String name;
        private final int maxStackSize;
        private final boolean diggable;
        private final boolean transparent;
        private final int emitLight;
        private final int filterLight;
        private final Class<? extends Metadata> metadataClass;
        private final Class<? extends BlockEntity> blockEntityClass;

        public IntBlock(int id, String name, int maxStackSize, boolean diggable, boolean transparent, int emitLight, int filterLight, Class<? extends Metadata> aClass, Class<? extends BlockEntity> blockEntityClass) {
            this.id = id;
            this.name = name;
            this.maxStackSize = maxStackSize;
            this.diggable = diggable;
            this.transparent = transparent;
            this.emitLight = emitLight;
            this.filterLight = filterLight;
            this.metadataClass = aClass;
            this.blockEntityClass = blockEntityClass;

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
        public Class<? extends Metadata> getMetadataClass() {
            return metadataClass;
        }

        @Override
        public int getMaximumStackSize() {
            return maxStackSize;
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
        public String toString() {
            return getName();
        }
    }
}
