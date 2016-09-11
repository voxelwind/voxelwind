package com.voxelwind.server.game.item;

import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.server.game.item.behaviors.blocks.SimpleBlockItemBehavior;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BlockBehaviors {
    private static final TIntObjectMap<BlockBehavior> SPECIAL_BEHAVIORS;

    static {
        TIntObjectMap<BlockBehavior> behaviors = new TIntObjectHashMap<>();

        SPECIAL_BEHAVIORS = TCollections.unmodifiableMap(behaviors);
    }

    public static BlockBehavior getBlockBehavior(BlockType type) {
        BlockBehavior behavior = SPECIAL_BEHAVIORS.get(type.getId());
        if (behavior == null) {
            return SimpleBlockItemBehavior.INSTANCE;
        }
        return behavior;
    }
}
