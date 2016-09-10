package com.voxelwind.server.game.item;

import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.server.game.item.behaviors.NoopItemBehavior;
import com.voxelwind.server.game.item.behaviors.blocks.SimpleBlockItemBehavior;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemBehaviors {
    private static final TIntObjectMap<ItemBehavior> SPECIAL_BEHAVIORS;

    static {
        TIntObjectMap<ItemBehavior> behaviors = new TIntObjectHashMap<ItemBehavior>();

        SPECIAL_BEHAVIORS = TCollections.unmodifiableMap(behaviors);
    }

    public static ItemBehavior getItemBehavior(ItemType type) {
        ItemBehavior behavior = SPECIAL_BEHAVIORS.get(type.getId());
        if (behavior == null) {
            return type.isBlock() ? SimpleBlockItemBehavior.INSTANCE : NoopItemBehavior.INSTANCE;
        }
        return behavior;
    }
}
