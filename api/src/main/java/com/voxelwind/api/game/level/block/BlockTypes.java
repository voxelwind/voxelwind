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
import lombok.experimental.UtilityClass;

import java.util.*;

/**
 * This class contains all block types recognized by Voxelwind and Pocket Edition.
 */
@UtilityClass
public class BlockTypes {
    private static Map<Integer, BlockType> BY_ID = new HashMap<>();
    private static final Random RANDOM = new Random();

    public static final BlockType AIR = IntBlock.builder().id(0).name("AIR").maxStackSize(0).diggable(false).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType STONE = IntBlock.builder().id(1).name("STONE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfForInDrop.ALL_PICKAXES).build();
    public static final BlockType DIRT = IntBlock.builder().id(3).name("DIRT").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GRASS_BLOCK = IntBlock.builder().id(2).name("GRASS_BLOCK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        // TODO: Handle silk touch
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(DIRT)
                .amount(1)
                .build());
    }).build();
    public static final BlockType COBBLESTONE = IntBlock.builder().id(4).name("COBBLESTONE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WOOD_PLANKS = IntBlock.builder().id(5).name("WOOD_PLANKS").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType SAPLING = IntBlock.builder().id(6).name("SAPLING").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BEDROCK = IntBlock.builder().id(7).name("BEDROCK").maxStackSize(64).diggable(false).transparent(false).emitLight(0).filterLight(15).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType WATER = IntBlock.builder().id(8).name("WATER").maxStackSize(0).diggable(false).transparent(true).emitLight(0).filterLight(2).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType STATIONARY_WATER = IntBlock.builder().id(9).name("STATIONARY_WATER").maxStackSize(0).diggable(false).transparent(true).emitLight(0).filterLight(2).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType LAVA = IntBlock.builder().id(10).name("LAVA").maxStackSize(0).diggable(false).transparent(true).emitLight(15).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType STATIONARY_LAVA = IntBlock.builder().id(11).name("STATIONARY_LAVA").maxStackSize(0).diggable(false).transparent(true).emitLight(15).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType SAND = IntBlock.builder().id(12).name("SAND").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GRAVEL = IntBlock.builder().id(13).name("GRAVEL").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GOLD_ORE = IntBlock.builder().id(14).name("GOLD_ORE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType IRON_ORE = IntBlock.builder().id(15).name("IRON_ORE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType COAL_ORE = IntBlock.builder().id(16).name("COAL_ORE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        if (ItemTypeUtil.isPickaxe(with.getItemType())) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.COAL)
                    .amount(1)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType WOOD = IntBlock.builder().id(17).name("WOOD").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType LEAVES = IntBlock.builder().id(18).name("LEAVES").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        if (with.getItemType() == ItemTypes.SHEARS) {
            return ImmutableList.of(tryExact(server, block));
        }

        // TODO: Drop saplings and apples. (http://minecraft.gamepedia.com/Leaves)
        return ImmutableList.of();
    }).build();
    public static final BlockType SPONGE = IntBlock.builder().id(19).name("SPONGE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GLASS = IntBlock.builder().id(20).name("GLASS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType LAPIS_LAZULI_ORE = IntBlock.builder().id(21).name("LAPIS_LAZULI_ORE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, i2, i3) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.DYE)
                .itemData(Dyed.of(DyeColor.BLUE))
                .amount((4) + RANDOM.nextInt(5))
                .build());
    }).build();
    public static final BlockType LAPIS_LAZULI_BLOCK = IntBlock.builder().id(22).name("LAPIS_LAZULI_BLOCK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType DISPENSER = IntBlock.builder().id(23).name("DISPENSER").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType SANDSTONE = IntBlock.builder().id(24).name("SANDSTONE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NOTE_BLOCK = IntBlock.builder().id(25).name("NOTE_BLOCK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BED = IntBlock.builder().id(26).name("BED").maxStackSize(1).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.BED)
                .amount(1)
                .build());
    }).build();
    public static final BlockType POWERED_RAIL = IntBlock.builder().id(27).name("POWERED_RAIL").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType DETECTOR_RAIL = IntBlock.builder().id(28).name("DETECTOR_RAIL").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType COBWEB = IntBlock.builder().id(30).name("COBWEB").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        if (with.getItemType() == ItemTypes.SHEARS || with.getItemType() == ItemTypes.IRON_SWORD || with.getItemType() == ItemTypes.GOLD_SWORD ||
                with.getItemType() == ItemTypes.DIAMOND_SWORD) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(with.getItemType() == ItemTypes.SHEARS ? block.getBlockState().getBlockType() : ItemTypes.STRING)
                    .amount(1)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType TALL_GRASS = IntBlock.builder().id(31).name("TALL_GRASS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
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
    public static final BlockType DEAD_BUSH = IntBlock.builder().id(32).name("DEAD_BUSH").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType WOOL = IntBlock.builder().id(35).name("WOOL").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).blockDataClass(Dyed.class).fromBlockMetadata(Dyed::of).fromMetadata(Dyed::of).build();
    public static final BlockType DANDELION = IntBlock.builder().id(37).name("DANDELION").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType FLOWER = IntBlock.builder().id(38).name("FLOWER").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BROWN_MUSHROOM = IntBlock.builder().id(39).name("BROWN_MUSHROOM").maxStackSize(64).diggable(true).transparent(false).emitLight(1).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType RED_MUSHROOM = IntBlock.builder().id(40).name("RED_MUSHROOM").maxStackSize(64).diggable(true).transparent(false).emitLight(1).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GOLD_BLOCK = IntBlock.builder().id(41).name("GOLD_BLOCK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType IRON_BLOCK = IntBlock.builder().id(42).name("IRON_BLOCK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    // TODO: Verify the next two
    public static final BlockType DOUBLE_STONE_SLAB = IntBlock.builder().id(43).name("DOUBLE_STONE_SLAB").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfForInDrop.ALL_PICKAXES).build();
    public static final BlockType STONE_SLAB = IntBlock.builder().id(44).name("STONE_SLAB").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfForInDrop.ALL_PICKAXES).build();
    public static final BlockType BRICKS = IntBlock.builder().id(45).name("BRICKS").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType TNT = IntBlock.builder().id(46).name("TNT").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BOOKSHELF = IntBlock.builder().id(47).name("BOOKSHELF").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.BOOK)
                .amount(3)
                .build());
    }).build();
    public static final BlockType MOSS_STONE = IntBlock.builder().id(48).name("MOSS_STONE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType OBSIDIAN = IntBlock.builder().id(49).name("OBSIDIAN").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType TORCH = IntBlock.builder().id(50).name("TORCH").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType FIRE = IntBlock.builder().id(51).name("FIRE").maxStackSize(0).diggable(true).transparent(true).emitLight(15).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType MONSTER_SPAWNER = IntBlock.builder().id(52).name("MONSTER_SPAWNER").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType OAK_WOOD_STAIRS = IntBlock.builder().id(53).name("OAK_WOOD_STAIRS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CHEST = IntBlock.builder().id(54).name("CHEST").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_WIRE = IntBlock.builder().id(55).name("REDSTONE_WIRE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.REDSTONE)
                .amount(1)
                .build());
    }).build();
    public static final BlockType DIAMOND_ORE = IntBlock.builder().id(56).name("DIAMOND_ORE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.DIAMOND)
                .amount(1)
                .build());
    }).build();
    public static final BlockType DIAMOND_BLOCK = IntBlock.builder().id(57).name("DIAMOND_BLOCK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CRAFTING_TABLE = IntBlock.builder().id(58).name("CRAFTING_BLOCK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CROPS = IntBlock.builder().id(59).name("CROPS").maxStackSize(0).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
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
    public static final BlockType FARMLAND = IntBlock.builder().id(60).name("FARMLAND").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(DIRT)
                .amount(1)
                .build());
    }).build();
    public static final BlockType FURNACE = IntBlock.builder().id(61).name("FURNACE").maxStackSize(64).diggable(true).transparent(true).emitLight(13).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BURNING_FURNACE = IntBlock.builder().id(62).name("BURNING_FURNACE").maxStackSize(64).diggable(true).transparent(true).emitLight(13).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(FURNACE)
                .amount(1)
                .build());
    }).build();
    public static final BlockType SIGN = IntBlock.builder().id(63).name("SIGN").maxStackSize(16).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.SIGN)
                .amount(1)
                .build());
    }).build();
    public static final BlockType WOODEN_DOOR = IntBlock.builder().id(64).name("WOODEN_DOOR").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType LADDER = IntBlock.builder().id(65).name("LADDER").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType RAIL = IntBlock.builder().id(66).name("RAIL").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType COBBLESTONE_STAIRS = IntBlock.builder().id(67).name("COBBLESTONE_STAIRS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WALL_SIGN = IntBlock.builder().id(68).name("WALL_SIGN").maxStackSize(16).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.SIGN)
                .amount(1)
                .build());
    }).build();
    public static final BlockType LEVER = IntBlock.builder().id(69).name("LEVER").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType STONE_PRESSURE_PLATE = IntBlock.builder().id(70).name("STONE_PRESSURE_PLATE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType IRON_DOOR = IntBlock.builder().id(71).name("IRON_DOOR").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WOODEN_PRESSURE_PLATE = IntBlock.builder().id(72).name("WOODEN_PRESSURE_PLATE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_ORE = IntBlock.builder().id(73).name("REDSTONE_ORE").maxStackSize(64).diggable(true).transparent(true).emitLight(9).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.REDSTONE)
                .amount(4 + RANDOM.nextInt(2))
                .build());
    }).build();
    public static final BlockType GLOWING_REDSTONE_ORE = IntBlock.builder().id(74).name("GLOWING_REDSTONE_ORE").maxStackSize(64).diggable(true).transparent(true).emitLight(9).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.REDSTONE)
                .amount(4 + RANDOM.nextInt(2))
                .build());
    }).build();
    public static final BlockType REDSTONE_TORCH = IntBlock.builder().id(75).name("REDSTONE_TORCH").maxStackSize(64).diggable(true).transparent(true).emitLight(7).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_TORCH_ACTIVE = IntBlock.builder().id(76).name("REDSTONE_TORCH_ACTIVE").maxStackSize(64).diggable(true).transparent(true).emitLight(7).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(REDSTONE_TORCH)
                .amount(1)
                .build());
    }).build();
    public static final BlockType STONE_BUTTON = IntBlock.builder().id(77).name("STONE_BUTTON").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType TOP_SNOW = IntBlock.builder().id(78).name("TOP_SNOW").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        if (ItemTypeUtil.isShovel(with.getItemType()) && RANDOM.nextBoolean()) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.SNOWBALL)
                    .amount(1)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType ICE = IntBlock.builder().id(79).name("ICE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType SNOW = IntBlock.builder().id(80).name("SNOW").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        if (ItemTypeUtil.isShovel(with.getItemType()) && RANDOM.nextBoolean()) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.SNOWBALL)
                    .amount(4)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType CACTUS = IntBlock.builder().id(81).name("CACTUS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CLAY = IntBlock.builder().id(82).name("CLAY").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        if (ItemTypeUtil.isShovel(with.getItemType()) && RANDOM.nextBoolean()) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(ItemTypes.CLAY)
                    .amount(4)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType SUGAR_CANE = IntBlock.builder().id(83).name("SUGAR_CANE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType FENCE = IntBlock.builder().id(85).name("FENCE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType PUMPKIN = IntBlock.builder().id(86).name("PUMPKIN").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NETHERRACK = IntBlock.builder().id(87).name("NETHERRACK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType SOUL_SAND = IntBlock.builder().id(88).name("SOUL_SAND").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GLOWSTONE = IntBlock.builder().id(89).name("GLOWSTONE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        // TODO: Handle silk touch
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.GLOWSTONE_DUST)
                .amount(2 + RANDOM.nextInt(3))
                .build());
    }).build();
    public static final BlockType PORTAL = IntBlock.builder().id(90).name("PORTAL").maxStackSize(0).diggable(false).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType JACK_O_LANTERN = IntBlock.builder().id(91).name("JACK_O_LANTERN").maxStackSize(64).diggable(true).transparent(true).emitLight(15).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CAKE = IntBlock.builder().id(92).name("CAKE").maxStackSize(1).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType REDSTONE_REPEATER = IntBlock.builder().id(93).name("REDSTONE_REPEATER").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_REPEATER_ACTIVE = IntBlock.builder().id(94).name("REDSTONE_REPEATER_ACTIVE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType INVISIBLE_BEDROCK = IntBlock.builder().id(95).name("INVISIBLE_BEDROCK").maxStackSize(64).diggable(false).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType TRAPDOOR = IntBlock.builder().id(96).name("TRAPDOOR").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType MONSTER_EGG = IntBlock.builder().id(97).name("MONSTER_EGG").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType STONE_BRICK = IntBlock.builder().id(98).name("STONE_BRICK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BROWN_MUSHROOM_BLOCK = IntBlock.builder().id(99).name("BROWN_MUSHROOM_BLOCK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        int amount = RANDOM.nextInt(3);
        if (amount > 0) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(BROWN_MUSHROOM)
                    .amount(amount)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType RED_MUSHROOM_BLOCK = IntBlock.builder().id(100).name("RED_MUSHROOM_BLOCK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        int amount = RANDOM.nextInt(3);
        if (amount > 0) {
            return ImmutableList.of(server.createItemStackBuilder()
                    .itemType(RED_MUSHROOM)
                    .amount(amount)
                    .build());
        }
        return ImmutableList.of();
    }).build();
    public static final BlockType IRON_BARS = IntBlock.builder().id(101).name("IRON_BARS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GLASS_PANE = IntBlock.builder().id(102).name("GLASS_PANE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType MELON = IntBlock.builder().id(103).name("MELON").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.MELON)
                .amount(RANDOM.nextInt(5) + 3)
                .build());
    }).build();
    public static final BlockType PUMPKIN_STEM = IntBlock.builder().id(104).name("PUMPKIN_STEM").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
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
    public static final BlockType MELON_STEM = IntBlock.builder().id(105).name("MELON_STEM").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
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
    public static final BlockType VINES = IntBlock.builder().id(106).name("VINES").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfForInDrop.SHEARS_ONLY).build();
    public static final BlockType FENCE_GATE = IntBlock.builder().id(107).name("FENCE_GATE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BRICK_STAIRS = IntBlock.builder().id(108).name("BRICK_STAIRS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType STONE_BRICK_STAIRS = IntBlock.builder().id(109).name("STONE_BRICK_STAIRS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType MYCELIUM = IntBlock.builder().id(110).name("MYCELIUM").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        // TODO: Silk touch
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(DIRT)
                .amount(1)
                .build());
    }).build();
    public static final BlockType LILY_PAD = IntBlock.builder().id(111).name("LILY_PAD").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NETHER_BRICK = IntBlock.builder().id(112).name("NETHER_BRICK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NETHER_BRICK_FENCE = IntBlock.builder().id(113).name("NETHER_BRICK_FENCE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NETHER_BRICK_STAIRS = IntBlock.builder().id(114).name("NETHER_BRICK_STAIRS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NETHER_WART = IntBlock.builder().id(115).name("NETHER_WART").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        // TODO: Implement NetherWart class
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.NETHER_WART)
                .amount(1)
                .build());
    }).build();
    public static final BlockType ENCHANTMENT_TABLE = IntBlock.builder().id(116).name("ENCHANTMENT_TABLE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BREWING_STAND = IntBlock.builder().id(117).name("BREWING_STAND").maxStackSize(64).diggable(true).transparent(true).emitLight(1).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CAULDRON = IntBlock.builder().id(118).name("CAULDRON").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType END_PORTAL_FRAME = IntBlock.builder().id(120).name("END_PORTAL_FRAME").maxStackSize(64).diggable(false).transparent(true).emitLight(1).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType END_STONE = IntBlock.builder().id(121).name("END_STONE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_LAMP = IntBlock.builder().id(122).name("REDSTONE_LAMP").maxStackSize(64).diggable(true).transparent(true).emitLight(15).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_LAMP_ACTIVE = IntBlock.builder().id(123).name("REDSTONE_LAMP_ACTIVE").maxStackSize(64).diggable(true).transparent(true).emitLight(15).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType ACTIVATOR_RAIL = IntBlock.builder().id(126).name("ACTIVATOR_RAIL").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType COCOA = IntBlock.builder().id(127).name("COCOA").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        // TODO: Implement cocoa class.
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.DYE)
                .itemData(Dyed.of(DyeColor.BROWN))
                .amount(1)
                .build());
    }).build();
    public static final BlockType SANDSTONE_STAIRS = IntBlock.builder().id(128).name("SANDSTONE_STAIRS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType EMERALD_ORE = IntBlock.builder().id(129).name("EMERALD_ORE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.EMERALD)
                .amount(1)
                .build());
    }).build();
    public static final BlockType TRIPWIRE_HOOK = IntBlock.builder().id(131).name("TRIPWIRE_HOOK").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType TRIPWIRE = IntBlock.builder().id(132).name("TRIPWIRE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.STRING)
                .amount(1)
                .build());
    }).build();
    public static final BlockType EMERALD_BLOCK = IntBlock.builder().id(133).name("EMERALD_BLOCK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType SPRUCE_WOOD_STAIRS = IntBlock.builder().id(134).name("SPRUCE_WOOD_STAIRS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BIRCH_WOOD_STAIRS = IntBlock.builder().id(135).name("BIRCH_WOOD_STAIRS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType JUNGLE_WOOD_STAIRS = IntBlock.builder().id(136).name("JUNGLE_WOOD_STAIRS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType COBBLESTONE_WALL = IntBlock.builder().id(139).name("COBBLESTONE_WALL").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType FLOWER_POT = IntBlock.builder().id(140).name("FLOWER_POT").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CARROTS = IntBlock.builder().id(141).name("CARROTS").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.CARROT)
                .amount(1 + RANDOM.nextInt(4))
                .build());
    }).fromBlockMetadata(Crops::ofStage).blockDataClass(Crops.class).build();
    public static final BlockType POTATO = IntBlock.builder().id(142).name("POTATO").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        // TODO: 2% chance of poisonous potato
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.POTATO)
                .amount(1 + RANDOM.nextInt(4))
                .build());
    }).fromBlockMetadata(Crops::ofStage).blockDataClass(Crops.class).build();
    public static final BlockType WOODEN_BUTTON = IntBlock.builder().id(143).name("WOODEN_BUTTON").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType MOB_HEAD = IntBlock.builder().id(144).name("MOB_HEAD").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType ANVIL = IntBlock.builder().id(145).name("ANVIL").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType TRAPPED_CHEST = IntBlock.builder().id(146).name("TRAPPED_CHEST").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WEIGHTED_PRESSURE_PLATE_LIGHT = IntBlock.builder().id(147).name("WEIGHTED_PRESSURE_PLATE_LIGHT").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WEIGHTED_PRESSURE_PLATE_HEAVY = IntBlock.builder().id(148).name("WEIGHTED_PRESSURE_PLATE_HEAVY").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType DAYLIGHT_SENSOR = IntBlock.builder().id(151).name("DAYLIGHT_SENSOR").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType REDSTONE_BLOCK = IntBlock.builder().id(152).name("REDSTONE_BLOCK").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType NETHER_QUARTZ_ORE = IntBlock.builder().id(153).name("NETHER_QUARTZ_ORE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.NETHER_QUARTZ)
                .amount(1)
                .build());
    }).build();
    public static final BlockType QUARTZ_BLOCK = IntBlock.builder().id(155).name("QUARTZ_BLOCK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType QUARTZ_STAIRS = IntBlock.builder().id(156).name("QUARTZ_STAIRS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    // TODO: Verify next two
    public static final BlockType WOODEN_DOUBLE_SLAB = IntBlock.builder().id(157).name("WOODEN_DOUBLE_SLAB").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType WOODEN_SLAB = IntBlock.builder().id(158).name("WOODEN_SLAB").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType STAINED_CLAY = IntBlock.builder().id(159).name("STAINED_CLAY").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).fromMetadata(Dyed::of).blockDataClass(Dyed.class).fromBlockMetadata(Dyed::of).build();
    public static final BlockType ACACIA_LEAVES = IntBlock.builder().id(161).name("ACACIA_LEAVES").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        // TODO: Handle saplings
        return ImmutableList.of();
    }).build();
    public static final BlockType ACACIA_WOOD = IntBlock.builder().id(162).name("ACACIA_WOOD").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType ACACIA_WOOD_STAIRS = IntBlock.builder().id(163).name("ACACIA_WOOD_STAIRS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType DARK_OAK_WOOD_STAIRS = IntBlock.builder().id(164).name("DARK_OAK_WOOD_STAIRS").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType IRON_TRAPDOOR = IntBlock.builder().id(167).name("IRON_TRAPDOOR").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType HAY_BALE = IntBlock.builder().id(170).name("HAY_BALE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType CARPET = IntBlock.builder().id(171).name("CARPET").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).fromMetadata(Dyed::of).blockDataClass(Dyed.class).fromBlockMetadata(Dyed::of).build();
    public static final BlockType HARDENED_CLAY = IntBlock.builder().id(172).name("HARDENED_CLAY").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType COAL_BLOCK = IntBlock.builder().id(173).name("COAL_BLOCK").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType PACKED_ICE = IntBlock.builder().id(174).name("PACKED_ICE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(NothingDrop.INSTANCE).build();
    // TODO: Fix sunfrlower
    public static final BlockType SUNFLOWER = IntBlock.builder().id(175).name("SUNFLOWER").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType INVERTED_DAYLIGHT_SENSOR = IntBlock.builder().id(178).name("INVERTED_DAYLIGHT_SENSOR").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType SPRUCE_FENCE_GATE = IntBlock.builder().id(183).name("SPRUCE_FENCE_GATE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType BIRCH_FENCE_GATE = IntBlock.builder().id(184).name("BIRCH_FENCE_GATE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType JUNGLE_FENCE_GATE = IntBlock.builder().id(185).name("JUNGLE_FENCE_GATE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType DARK_OAK_FENCE_GATE = IntBlock.builder().id(186).name("DARK_OAK_FENCE_GATE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType ACACIA_FENCE_GATE = IntBlock.builder().id(187).name("ACACIA_FENCE_GATE").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GRASS_PATH = IntBlock.builder().id(198).name("GRASS_PATH").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType ITEM_FRAME = IntBlock.builder().id(199).name("ITEM_FRAME").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType PODZOL = IntBlock.builder().id(243).name("PODZOL").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
        // TODO: Handle silk touch
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(DIRT)
                .amount(1)
                .build());
    }).build();
    public static final BlockType BEETROOT = IntBlock.builder().id(244).name("BEETROOT").maxStackSize(64).diggable(true).transparent(true).emitLight(0).filterLight(0).dropHandler((server, block, with) -> {
        // TODO: Handle beetroots correctly
        return ImmutableList.of(server.createItemStackBuilder()
                .itemType(ItemTypes.BEETROOT_SEEDS)
                .amount(1)
                .build());
    }).build();
    public static final BlockType STONECUTTER = IntBlock.builder().id(245).name("STONECUTTER").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler(SelfDrop.INSTANCE).build();
    public static final BlockType GLOWING_OBSIDIAN = IntBlock.builder().id(246).name("GLOWING_OBSIDIAN").maxStackSize(64).diggable(true).transparent(false).emitLight(12).filterLight(15).dropHandler(NothingDrop.INSTANCE).build();
    public static final BlockType NETHER_REACTOR_CORE = IntBlock.builder().id(247).name("NETHER_REACTOR_CORE").maxStackSize(64).diggable(true).transparent(false).emitLight(0).filterLight(15).dropHandler((server, block, with) -> {
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
        private final String name;
        private final int maxStackSize;
        private final boolean diggable;
        private final boolean transparent;
        private final int emitLight;
        private final int filterLight;
        private final DroppedHandler dropHandler;
        private final Class<? extends BlockData> blockDataClass;
        private final ItemTypes.FromMetadata fromMetadata;
        private final FromBlockMetadata fromBlockMetadata;

        public IntBlock(int id, String name, int maxStackSize, boolean diggable, boolean transparent, int emitLight, int filterLight, DroppedHandler dropHandler, Class<? extends BlockData> aClass, ItemTypes.FromMetadata fromMetadata, FromBlockMetadata fromBlockMetadata) {
            this.id = id;
            this.name = name;
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
        public String getName() {
            return name;
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

        @Override
        public String toString() {
            return getName();
        }
    }
}
