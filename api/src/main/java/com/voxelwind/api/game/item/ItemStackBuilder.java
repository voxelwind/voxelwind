package com.voxelwind.api.game.item;

import com.voxelwind.api.game.item.data.MaterialData;
import com.voxelwind.api.server.Server;

import javax.annotation.Nonnull;

/**
 * This interface specifies a builder for item stacks. You can access an instance of this interface using
 * {@link Server#createItemStackBuilder()}.
 */
public interface ItemStackBuilder {
    /**
     * Specifies the material to use.
     * @param material the material to use
     * @return the builder, for chaining
     */
    ItemStackBuilder material(@Nonnull Material material);

    /**
     * Specifies the amount of items to give in this item stack.
     * @param amount the amount
     * @return the builder, for chaining
     */
    ItemStackBuilder amount(int amount);

    /**
     * Specifies the data to use for this item. This requires a material already be set.
     * @param data
     * @return the builder, for chaining
     */
    ItemStackBuilder materialData(@Nonnull MaterialData data);

    // TODO: Add more methods as more functionality is implemented

    /**
     * Creates the {@link ItemStack).
     * @return the item stack
     */
    ItemStack build();
}
