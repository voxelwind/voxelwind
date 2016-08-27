package com.voxelwind.api.game.item;

import com.voxelwind.api.game.item.data.Coal;
import com.voxelwind.api.game.item.data.Dyed;
import com.voxelwind.api.game.item.data.GenericDamageValue;
import com.voxelwind.api.game.item.data.ItemData;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents all items available on the server.
 */
public class ItemTypes {
    private static Map<Integer, IntItem> BY_ID = new HashMap<>();

    public static final ItemType IRON_SHOVEL = new IntItem(256, "iron_shovel", 1, null);
    public static final ItemType IRON_PICKAXE = new IntItem(257, "iron_pickaxe", 1, null);
    public static final ItemType IRON_AXE = new IntItem(258, "iron_axe", 1, null);
    public static final ItemType FLINT_AND_STEEL = new IntItem(259, "flint_and_steel", 1, null);
    public static final ItemType APPLE = new IntItem(260, "apple", 64, null);
    public static final ItemType BOW = new IntItem(261, "bow", 1, null);
    public static final ItemType ARROW = new IntItem(262, "arrow", 64, null);
    public static final ItemType COAL = new IntItem(263, "coal", 64, null, Coal::of);
    public static final ItemType DIAMOND = new IntItem(264, "diamond", 64, null);
    public static final ItemType IRON_INGOT = new IntItem(265, "iron_ingot", 64, null);
    public static final ItemType GOLD_INGOT = new IntItem(266, "gold_ingot", 64, null);
    public static final ItemType IRON_SWORD = new IntItem(267, "iron_sword", 1, null);
    public static final ItemType WOODEN_SWORD = new IntItem(268, "wooden_sword", 1, null);
    public static final ItemType WOODEN_SHOVEL = new IntItem(269, "wooden_shovel", 1, null);
    public static final ItemType WOODEN_PICKAXE = new IntItem(270, "wooden_pickaxe", 1, null);
    public static final ItemType WOODEN_AXE = new IntItem(271, "wooden_axe", 1, null);
    public static final ItemType STONE_SWORD = new IntItem(272, "stone_sword", 1, null);
    public static final ItemType STONE_SHOVEL = new IntItem(273, "stone_shovel", 1, null);
    public static final ItemType STONE_PICKAXE = new IntItem(274, "stone_pickaxe", 1, null);
    public static final ItemType STONE_AXE = new IntItem(275, "stone_axe", 1, null);
    public static final ItemType DIAMOND_SWORD = new IntItem(276, "diamond_sword", 1, null);
    public static final ItemType DIAMOND_SHOVEL = new IntItem(277, "diamond_shovel", 1, null);
    public static final ItemType DIAMOND_PICKAXE = new IntItem(278, "diamond_pickaxe", 1, null);
    public static final ItemType DIAMOND_AXE = new IntItem(279, "diamond_axe", 1, null);
    public static final ItemType STICK = new IntItem(280, "stick", 64, null);
    public static final ItemType BOWL = new IntItem(281, "bowl", 64, null);
    public static final ItemType MUSHROOM_STEW = new IntItem(282, "mushroom_stew", 1, null);
    public static final ItemType GOLD_SWORD = new IntItem(283, "gold_sword", 1, null);
    public static final ItemType GOLD_SHOVEL = new IntItem(284, "gold_shovel", 1, null);
    public static final ItemType GOLD_PICKAXE = new IntItem(285, "gold_pickaxe", 1, null);
    public static final ItemType GOLD_AXE = new IntItem(286, "gold_axe", 1, null);
    public static final ItemType STRING = new IntItem(287, "string", 64, null);
    public static final ItemType FEATHER = new IntItem(288, "feather", 64, null);
    public static final ItemType GUNPOWDER = new IntItem(289, "gunpowder", 64, null);
    public static final ItemType WOODEN_HOE = new IntItem(290, "wooden_hoe", 1, null);
    public static final ItemType STONE_HOE = new IntItem(291, "stone_hoe", 1, null);
    public static final ItemType IRON_HOE = new IntItem(292, "iron_hoe", 1, null);
    public static final ItemType DIAMOND_HOE = new IntItem(293, "diamond_hoe", 1, null);
    public static final ItemType GOLD_HOE = new IntItem(294, "gold_hoe", 1, null);
    public static final ItemType SEEDS = new IntItem(295, "seeds", 64, null);
    public static final ItemType WHEAT = new IntItem(296, "wheat", 64, null);
    public static final ItemType BREAD = new IntItem(297, "bread", 64, null);
    public static final ItemType LEATHER_CAP = new IntItem(298, "leather_cap", 1, null);
    public static final ItemType LEATHER_TUNIC = new IntItem(299, "leather_tunic", 1, null);
    public static final ItemType LEATHER_PANTS = new IntItem(300, "leather_pants", 1, null);
    public static final ItemType LEATHER_BOOTS = new IntItem(301, "leather_boots", 1, null);
    public static final ItemType CHAIN_HELMET = new IntItem(302, "chain_helmet", 1, null);
    public static final ItemType CHAIN_CHESTPLATE = new IntItem(303, "chain_chestplate", 1, null);
    public static final ItemType CHAIN_LEGGINGS = new IntItem(304, "chain_leggings", 1, null);
    public static final ItemType CHAIN_BOOTS = new IntItem(305, "chain_boots", 1, null);
    public static final ItemType IRON_HELMET = new IntItem(306, "iron_helmet", 1, null);
    public static final ItemType IRON_CHESTPLATE = new IntItem(307, "iron_chestplate", 1, null);
    public static final ItemType IRON_LEGGINGS = new IntItem(308, "iron_leggings", 1, null);
    public static final ItemType IRON_BOOTS = new IntItem(309, "iron_boots", 1, null);
    public static final ItemType DIAMOND_HELMET = new IntItem(310, "diamond_helmet", 1, null);
    public static final ItemType DIAMOND_CHESTPLATE = new IntItem(311, "diamond_chestplate", 1, null);
    public static final ItemType DIAMOND_LEGGINGS = new IntItem(312, "diamond_leggings", 1, null);
    public static final ItemType DIAMOND_BOOTS = new IntItem(313, "diamond_boots", 1, null);
    public static final ItemType GOLDEN_HELMET = new IntItem(314, "golden_helmet", 1, null);
    public static final ItemType GOLDEN_CHESTPLATE = new IntItem(315, "golden_chestplate", 1, null);
    public static final ItemType GOLDEN_LEGGINGS = new IntItem(316, "golden_leggings", 1, null);
    public static final ItemType GOLDEN_BOOTS = new IntItem(317, "golden_boots", 1, null);
    public static final ItemType FLINT = new IntItem(318, "flint", 64, null);
    public static final ItemType RAW_PORKCHOP = new IntItem(319, "raw_porkchop", 64, null);
    public static final ItemType COOKED_PORKCHOP = new IntItem(320, "cooked_porkchop", 64, null);
    public static final ItemType PAINTING = new IntItem(321, "painting", 64, null);
    public static final ItemType GOLDEN_APPLE = new IntItem(322, "golden_apple", 64, null);
    public static final ItemType SIGN = new IntItem(323, "sign", 16, null);
    public static final ItemType WOODEN_DOOR = new IntItem(324, "wooden_door", 64, null);
    public static final ItemType BUCKET = new IntItem(325, "bucket", 16, null);
    public static final ItemType MINECART = new IntItem(328, "minecart", 1, null);
    public static final ItemType SADDLE = new IntItem(329, "saddle", 1, null);
    public static final ItemType IRON_DOOR = new IntItem(330, "iron_door", 64, null);
    public static final ItemType REDSTONE = new IntItem(331, "redstone", 64, null);
    public static final ItemType SNOWBALL = new IntItem(332, "snowball", 16, null);
    public static final ItemType BOAT = new IntItem(333, "boat", 1, null);
    public static final ItemType LEATHER = new IntItem(334, "leather", 64, null);
    public static final ItemType BRICK = new IntItem(336, "brick", 64, null);
    public static final ItemType CLAY = new IntItem(337, "clay", 64, null);
    public static final ItemType SUGAR_CANE = new IntItem(338, "sugar_cane", 64, null);
    public static final ItemType PAPER = new IntItem(339, "paper", 64, null);
    public static final ItemType BOOK = new IntItem(340, "book", 64, null);
    public static final ItemType SLIMEBALL = new IntItem(341, "slimeball", 64, null);
    public static final ItemType EGG = new IntItem(344, "egg", 16, null);
    public static final ItemType COMPASS = new IntItem(345, "compass", 64, null);
    public static final ItemType FISHING_ROD = new IntItem(346, "fishing_rod", 1, null);
    public static final ItemType CLOCK = new IntItem(347, "clock", 64, null);
    public static final ItemType GLOWSTONE_DUST = new IntItem(348, "glowstone_dust", 64, null);
    public static final ItemType RAW_FISH = new IntItem(349, "raw_fish", 64, null);
    public static final ItemType COOKED_FISH = new IntItem(350, "cooked_fish", 64, null);
    public static final ItemType DYE = new IntItem(351, "dye", 64, Dyed.class, Dyed::of);
    public static final ItemType BONE = new IntItem(352, "bone", 64, null);
    public static final ItemType SUGAR = new IntItem(353, "sugar", 64, null);
    public static final ItemType CAKE = new IntItem(354, "cake", 1, null);
    public static final ItemType BED = new IntItem(355, "bed", 1, null);
    public static final ItemType REDSTONE_REPEATER = new IntItem(356, "redstone_repeater", 64, null);
    public static final ItemType COOKIE = new IntItem(357, "cookie", 64, null);
    public static final ItemType SHEARS = new IntItem(359, "shears", 1, null);
    public static final ItemType MELON = new IntItem(360, "melon", 64, null);
    public static final ItemType PUMPKIN_SEEDS = new IntItem(361, "pumpkin_seeds", 64, null);
    public static final ItemType MELON_SEEDS = new IntItem(362, "melon_seeds", 64, null);
    public static final ItemType RAW_BEEF = new IntItem(363, "raw_beef", 64, null);
    public static final ItemType STEAK = new IntItem(364, "steak", 64, null);
    public static final ItemType RAW_CHICKEN = new IntItem(365, "raw_chicken", 64, null);
    public static final ItemType COOKED_CHICKEN = new IntItem(366, "cooked_chicken", 64, null);
    public static final ItemType ROTTEN_FLESH = new IntItem(367, "rotten_flesh", 64, null);
    public static final ItemType BLAZE_ROD = new IntItem(369, "blaze_rod", 64, null);
    public static final ItemType GHAST_TEAR = new IntItem(370, "ghast_tear", 64, null);
    public static final ItemType GOLD_NUGGET = new IntItem(371, "gold_nugget", 64, null);
    public static final ItemType NETHER_WART = new IntItem(372, "nether_wart", 64, null);
    public static final ItemType POTION = new IntItem(373, "potion", 1, null);
    public static final ItemType GLASS_BOTTLE = new IntItem(374, "glass_bottle", 64, null);
    public static final ItemType SPIDER_EYE = new IntItem(375, "spider_eye", 64, null);
    public static final ItemType FERMENTED_SPIDER_EYE = new IntItem(376, "fermented_spider_eye", 64, null);
    public static final ItemType BLAZE_POWDER = new IntItem(377, "blaze_powder", 64, null);
    public static final ItemType MAGMA_CREAM = new IntItem(378, "magma_cream", 64, null);
    public static final ItemType BREWING_STAND = new IntItem(379, "brewing_stand", 64, null);
    public static final ItemType CAULDRON = new IntItem(380, "cauldron", 64, null);
    public static final ItemType GLISTERING_MELON = new IntItem(382, "glistering_melon", 64, null);
    public static final ItemType SPAWN_EGG = new IntItem(383, "spawn_egg", 64, null);
    public static final ItemType BOTTLE_O_ENCHANTING = new IntItem(384, "bottle_o'_enchanting", 64, null);
    public static final ItemType EMERALD = new IntItem(388, "emerald", 64, null);
    public static final ItemType FLOWER_POT = new IntItem(390, "flower_pot", 64, null);
    public static final ItemType CARROT = new IntItem(391, "carrot", 64, null);
    public static final ItemType POTATO = new IntItem(392, "potato", 64, null);
    public static final ItemType BAKED_POTATO = new IntItem(393, "baked_potato", 64, null);
    public static final ItemType POISONOUS_POTATO = new IntItem(394, "poisonous_potato", 64, null);
    public static final ItemType GOLDEN_CARROT = new IntItem(396, "golden_carrot", 64, null);
    public static final ItemType MOB_HEAD = new IntItem(397, "mob_head", 64, null);
    public static final ItemType PUMPKIN_PIE = new IntItem(400, "pumpkin_pie", 64, null);
    public static final ItemType ENCHANTED_BOOK = new IntItem(403, "enchanted_book", 1, null);
    public static final ItemType NETHER_BRICK = new IntItem(405, "nether_brick", 64, null);
    public static final ItemType NETHER_QUARTZ = new IntItem(406, "nether_quartz", 64, null);
    public static final ItemType RAW_RABBIT = new IntItem(411, "raw_rabbit", 64, null);
    public static final ItemType COOKED_RABBIT = new IntItem(412, "cooked_rabbit", 64, null);
    public static final ItemType RABBIT_STEW = new IntItem(413, "rabbit_stew", 1, null);
    public static final ItemType RABBITS_FOOT = new IntItem(414, "rabbit's_foot", 64, null);
    public static final ItemType RABBIT_HIDE = new IntItem(415, "rabbit_hide", 64, null);
    public static final ItemType SPLASH_POTION = new IntItem(438, "splash_potion", 1, null);
    public static final ItemType BEETROOT = new IntItem(457, "beetroot", 64, null);
    public static final ItemType BEETROOT_SEEDS = new IntItem(458, "beetroot_seeds", 64, null);
    public static final ItemType BEETROOT_SOUP = new IntItem(459, "beetroot_soup", 1, null);
    public static final ItemType CAMERA = new IntItem(498, "camera", 64, null);

    public interface FromMetadata {
        ItemData of(short s);
    }

    public static ItemType forId(int data) {
        return forId(data, false);
    }

    public static ItemType forId(int data, boolean itemsOnly) {
        ItemType type = BY_ID.get(data);
        if (type == null) {
            if (itemsOnly) {
                throw new IllegalArgumentException("ID is not valid.");
            } else {
                return BlockTypes.forId(data);
            }
        }
        return type;
    }

    private static class IntItem implements ItemType {
        private final int id;
        private final String name;
        private final int maxStackSize;
        private final Class<? extends ItemData> data;
        private final FromMetadata fromMetadata;

        public IntItem(int id, String name, int maxStackSize, Class<? extends ItemData> data) {
            this(id, name, maxStackSize, data, null);
        }

        public IntItem(int id, String name, int maxStackSize, Class<? extends ItemData> data, FromMetadata fromMetadata) {
            this.id = id;
            this.name = name;
            this.maxStackSize = maxStackSize;
            this.data = data;
            this.fromMetadata = fromMetadata == null ? GenericDamageValue::new : fromMetadata;

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
        public boolean isBlock() {
            return false;
        }

        @Override
        public Class<? extends ItemData> getMaterialDataClass() {
            return data;
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
    }
}
