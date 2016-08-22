package com.voxelwind.api.game.item;

import com.voxelwind.api.game.item.data.ItemData;
import com.voxelwind.api.server.Server;

import javax.annotation.Nonnull;

/**
 * This interface specifies a builder for item stacks. You can access an instance of this interface using
 * {@link Server#createItemStackBuilder()}.
 */
public interface ItemStackBuilder {
    /**
     * Specifies the itemType to use.
     * @param itemType the itemType to use
     * @return the builder, for chaining
     */
    ItemStackBuilder material(@Nonnull ItemType itemType);

    /**
     * Specifies the amount of items to give in this item stack.
     * @param amount the amount
     * @return the builder, for chaining
     */
    ItemStackBuilder amount(int amount);

    /**
     * Specifies the data to use for this item. This requires a material already be set.
     * @param data the material data to use
     * @return the builder, for chaining
     */
    ItemStackBuilder itemData(ItemData data);

    // TODO: Add more methods as more functionality is implemented

    /**
     * Creates the {@link ItemStack).
     * @return the item stack
     */
    ItemStack build();
}
