package com.voxelwind.api.game.level.block;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.server.Server;

import java.util.Collection;

/**
 * This interface specifies a kind of block.
 */
public interface BlockType extends ItemType {
    default boolean isBlock() {
        return true;
    }

    boolean isDiggable();
    boolean isTransparent();

    int emitsLight();
    int filtersLight();

    @Deprecated
    Collection<ItemStack> getDrops(Server server, Block block, ItemStack with);

    Class<? extends BlockData> getBlockDataClass();
}
