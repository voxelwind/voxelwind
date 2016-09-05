package com.voxelwind.api.game.item.util;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;

@UtilityClass
public class ItemTypeUtil {
    public static boolean isShovel(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.DIAMOND_SHOVEL || type == ItemTypes.GOLD_SHOVEL || type == ItemTypes.IRON_SHOVEL ||
                type == ItemTypes.STONE_SHOVEL || type == ItemTypes.WOODEN_SHOVEL;
    }

    public static boolean isPickaxe(@Nonnull ItemType type) {
        Preconditions.checkNotNull(type, "type");
        return type == ItemTypes.DIAMOND_PICKAXE || type == ItemTypes.GOLD_PICKAXE || type == ItemTypes.IRON_PICKAXE ||
                type == ItemTypes.STONE_PICKAXE || type == ItemTypes.WOODEN_PICKAXE;
    }
}
