package com.voxelwind.api.game.item;

import com.voxelwind.api.game.item.data.Coal;
import com.voxelwind.api.game.item.data.Dyed;
import com.voxelwind.api.game.item.data.GenericDamageValue;
import com.voxelwind.api.game.item.data.ItemData;
import com.voxelwind.api.game.level.block.BlockTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents all items available on the server.
 */
public class ItemTypes {
    private static Map<Integer, IntItem> BY_ID = new HashMap<>();

    public static final ItemType IRON_SHOVEL = new IntItem(256, 1, null);
    public static final ItemType IRON_PICKAXE = new IntItem(257, 1, null);
    public static final ItemType IRON_AXE = new IntItem(258, 1, null);
    public static final ItemType FLINT_AND_STEEL = new IntItem(259, 1, null);
    public static final ItemType APPLE = new IntItem(260, 64, null);
    public static final ItemType BOW = new IntItem(261, 1, null);
    public static final ItemType ARROW = new IntItem(262, 64, null);
    public static final ItemType COAL = new IntItem(263, 64, null, Coal::of);
    public static final ItemType DIAMOND = new IntItem(264, 64, null);
    public static final ItemType IRON_INGOT = new IntItem(265, 64, null);
    public static final ItemType GOLD_INGOT = new IntItem(266, 64, null);
    public static final ItemType IRON_SWORD = new IntItem(267, 1, null);
    public static final ItemType WOODEN_SWORD = new IntItem(268, 1, null);
    public static final ItemType WOODEN_SHOVEL = new IntItem(269, 1, null);
    public static final ItemType WOODEN_PICKAXE = new IntItem(270, 1, null);
    public static final ItemType WOODEN_AXE = new IntItem(271, 1, null);
    public static final ItemType STONE_SWORD = new IntItem(272, 1, null);
    public static final ItemType STONE_SHOVEL = new IntItem(273, 1, null);
    public static final ItemType STONE_PICKAXE = new IntItem(274, 1, null);
    public static final ItemType STONE_AXE = new IntItem(275, 1, null);
    public static final ItemType DIAMOND_SWORD = new IntItem(276, 1, null);
    public static final ItemType DIAMOND_SHOVEL = new IntItem(277, 1, null);
    public static final ItemType DIAMOND_PICKAXE = new IntItem(278, 1, null);
    public static final ItemType DIAMOND_AXE = new IntItem(279, 1, null);
    public static final ItemType STICK = new IntItem(280, 64, null);
    public static final ItemType BOWL = new IntItem(281, 64, null);
    public static final ItemType MUSHROOM_STEW = new IntItem(282, 1, null);
    public static final ItemType GOLD_SWORD = new IntItem(283, 1, null);
    public static final ItemType GOLD_SHOVEL = new IntItem(284, 1, null);
    public static final ItemType GOLD_PICKAXE = new IntItem(285, 1, null);
    public static final ItemType GOLD_AXE = new IntItem(286, 1, null);
    public static final ItemType STRING = new IntItem(287, 64, null);
    public static final ItemType FEATHER = new IntItem(288, 64, null);
    public static final ItemType GUNPOWDER = new IntItem(289, 64, null);
    public static final ItemType WOODEN_HOE = new IntItem(290, 1, null);
    public static final ItemType STONE_HOE = new IntItem(291, 1, null);
    public static final ItemType IRON_HOE = new IntItem(292, 1, null);
    public static final ItemType DIAMOND_HOE = new IntItem(293, 1, null);
    public static final ItemType GOLD_HOE = new IntItem(294, 1, null);
    public static final ItemType SEEDS = new IntItem(295, 64, null);
    public static final ItemType WHEAT = new IntItem(296, 64, null);
    public static final ItemType BREAD = new IntItem(297, 64, null);
    public static final ItemType LEATHER_CAP = new IntItem(298, 1, null);
    public static final ItemType LEATHER_TUNIC = new IntItem(299, 1, null);
    public static final ItemType LEATHER_PANTS = new IntItem(300, 1, null);
    public static final ItemType LEATHER_BOOTS = new IntItem(301, 1, null);
    public static final ItemType CHAIN_HELMET = new IntItem(302, 1, null);
    public static final ItemType CHAIN_CHESTPLATE = new IntItem(303, 1, null);
    public static final ItemType CHAIN_LEGGINGS = new IntItem(304, 1, null);
    public static final ItemType CHAIN_BOOTS = new IntItem(305, 1, null);
    public static final ItemType IRON_HELMET = new IntItem(306, 1, null);
    public static final ItemType IRON_CHESTPLATE = new IntItem(307, 1, null);
    public static final ItemType IRON_LEGGINGS = new IntItem(308, 1, null);
    public static final ItemType IRON_BOOTS = new IntItem(309, 1, null);
    public static final ItemType DIAMOND_HELMET = new IntItem(310, 1, null);
    public static final ItemType DIAMOND_CHESTPLATE = new IntItem(311, 1, null);
    public static final ItemType DIAMOND_LEGGINGS = new IntItem(312, 1, null);
    public static final ItemType DIAMOND_BOOTS = new IntItem(313, 1, null);
    public static final ItemType GOLDEN_HELMET = new IntItem(314, 1, null);
    public static final ItemType GOLDEN_CHESTPLATE = new IntItem(315, 1, null);
    public static final ItemType GOLDEN_LEGGINGS = new IntItem(316, 1, null);
    public static final ItemType GOLDEN_BOOTS = new IntItem(317, 1, null);
    public static final ItemType FLINT = new IntItem(318, 64, null);
    public static final ItemType RAW_PORKCHOP = new IntItem(319, 64, null);
    public static final ItemType COOKED_PORKCHOP = new IntItem(320, 64, null);
    public static final ItemType PAINTING = new IntItem(321, 64, null);
    public static final ItemType GOLDEN_APPLE = new IntItem(322, 64, null);
    public static final ItemType SIGN = new IntItem(323, 16, null);
    public static final ItemType WOODEN_DOOR = new IntItem(324, 64, null);
    public static final ItemType BUCKET = new IntItem(325, 16, null);
    public static final ItemType MINECART = new IntItem(328, 1, null);
    public static final ItemType SADDLE = new IntItem(329, 1, null);
    public static final ItemType IRON_DOOR = new IntItem(330, 64, null);
    public static final ItemType REDSTONE = new IntItem(331, 64, null);
    public static final ItemType SNOWBALL = new IntItem(332, 16, null);
    public static final ItemType BOAT = new IntItem(333, 1, null);
    public static final ItemType LEATHER = new IntItem(334, 64, null);
    public static final ItemType BRICK = new IntItem(336, 64, null);
    public static final ItemType CLAY = new IntItem(337, 64, null);
    public static final ItemType SUGAR_CANE = new IntItem(338, 64, null);
    public static final ItemType PAPER = new IntItem(339, 64, null);
    public static final ItemType BOOK = new IntItem(340, 64, null);
    public static final ItemType SLIMEBALL = new IntItem(341, 64, null);
    public static final ItemType EGG = new IntItem(344, 16, null);
    public static final ItemType COMPASS = new IntItem(345, 64, null);
    public static final ItemType FISHING_ROD = new IntItem(346, 1, null);
    public static final ItemType CLOCK = new IntItem(347, 64, null);
    public static final ItemType GLOWSTONE_DUST = new IntItem(348, 64, null);
    public static final ItemType RAW_FISH = new IntItem(349, 64, null);
    public static final ItemType COOKED_FISH = new IntItem(350, 64, null);
    public static final ItemType DYE = new IntItem(351, 64, Dyed.class, Dyed::of);
    public static final ItemType BONE = new IntItem(352, 64, null);
    public static final ItemType SUGAR = new IntItem(353, 64, null);
    public static final ItemType CAKE = new IntItem(354, 1, null);
    public static final ItemType BED = new IntItem(355, 1, null);
    public static final ItemType REDSTONE_REPEATER = new IntItem(356, 64, null);
    public static final ItemType COOKIE = new IntItem(357, 64, null);
    public static final ItemType SHEARS = new IntItem(359, 1, null);
    public static final ItemType MELON = new IntItem(360, 64, null);
    public static final ItemType PUMPKIN_SEEDS = new IntItem(361, 64, null);
    public static final ItemType MELON_SEEDS = new IntItem(362, 64, null);
    public static final ItemType RAW_BEEF = new IntItem(363, 64, null);
    public static final ItemType STEAK = new IntItem(364, 64, null);
    public static final ItemType RAW_CHICKEN = new IntItem(365, 64, null);
    public static final ItemType COOKED_CHICKEN = new IntItem(366, 64, null);
    public static final ItemType ROTTEN_FLESH = new IntItem(367, 64, null);
    public static final ItemType BLAZE_ROD = new IntItem(369, 64, null);
    public static final ItemType GHAST_TEAR = new IntItem(370, 64, null);
    public static final ItemType GOLD_NUGGET = new IntItem(371, 64, null);
    public static final ItemType NETHER_WART = new IntItem(372, 64, null);
    public static final ItemType POTION = new IntItem(373, 1, null);
    public static final ItemType GLASS_BOTTLE = new IntItem(374, 64, null);
    public static final ItemType SPIDER_EYE = new IntItem(375, 64, null);
    public static final ItemType FERMENTED_SPIDER_EYE = new IntItem(376, 64, null);
    public static final ItemType BLAZE_POWDER = new IntItem(377, 64, null);
    public static final ItemType MAGMA_CREAM = new IntItem(378, 64, null);
    public static final ItemType BREWING_STAND = new IntItem(379, 64, null);
    public static final ItemType CAULDRON = new IntItem(380, 64, null);
    public static final ItemType GLISTERING_MELON = new IntItem(382, 64, null);
    public static final ItemType SPAWN_EGG = new IntItem(383, 64, null);
    public static final ItemType BOTTLE_O_ENCHANTING = new IntItem(384, 64, null);
    public static final ItemType EMERALD = new IntItem(388, 64, null);
    public static final ItemType FLOWER_POT = new IntItem(390, 64, null);
    public static final ItemType CARROT = new IntItem(391, 64, null);
    public static final ItemType POTATO = new IntItem(392, 64, null);
    public static final ItemType BAKED_POTATO = new IntItem(393, 64, null);
    public static final ItemType POISONOUS_POTATO = new IntItem(394, 64, null);
    public static final ItemType GOLDEN_CARROT = new IntItem(396, 64, null);
    public static final ItemType MOB_HEAD = new IntItem(397, 64, null);
    public static final ItemType PUMPKIN_PIE = new IntItem(400, 64, null);
    public static final ItemType ENCHANTED_BOOK = new IntItem(403, 1, null);
    public static final ItemType NETHER_BRICK = new IntItem(405, 64, null);
    public static final ItemType NETHER_QUARTZ = new IntItem(406, 64, null);
    public static final ItemType RAW_RABBIT = new IntItem(411, 64, null);
    public static final ItemType COOKED_RABBIT = new IntItem(412, 64, null);
    public static final ItemType RABBIT_STEW = new IntItem(413, 1, null);
    public static final ItemType RABBITS_FOOT = new IntItem(414, 64, null);
    public static final ItemType RABBIT_HIDE = new IntItem(415, 64, null);
    public static final ItemType SPLASH_POTION = new IntItem(438, 1, null);
    public static final ItemType BEETROOT = new IntItem(457, 64, null);
    public static final ItemType BEETROOT_SEEDS = new IntItem(458, 64, null);
    public static final ItemType BEETROOT_SOUP = new IntItem(459, 1, null);
    public static final ItemType CAMERA = new IntItem(498, 64, null);

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
        private final int maxStackSize;
        private final Class<? extends ItemData> data;
        private final FromMetadata fromMetadata;

        public IntItem(int id, int maxStackSize, Class<? extends ItemData> data) {
            this(id, maxStackSize, data, null);
        }

        public IntItem(int id, int maxStackSize, Class<? extends ItemData> data, FromMetadata fromMetadata) {
            this.id = id;
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
