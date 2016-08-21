package com.voxelwind.api.game.item;

import com.voxelwind.api.game.item.data.MaterialData;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Specifies an item stack. This class is immutable.
 */
@Nonnull
public interface ItemStack {
    /**
     * Returns the material this item stack represents.
     * @return the material the item stack represented
     */
    Material getMaterial();

    /**
     * Returns the amount that this item stack represents.
     * @return the amount this item stack represents
     */
    int getAmount();

    /**
     * Returns the material data that this item has.
     * @return the material data
     */
    Optional<MaterialData> getMaterialData();

    /**
     * Creates a builder from this item stack.
     * @return a {@link ItemStackBuilder}
     */
    ItemStackBuilder toBuilder();
}
