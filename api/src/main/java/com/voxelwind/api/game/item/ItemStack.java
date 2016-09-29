package com.voxelwind.api.game.item;

import com.voxelwind.api.game.Metadata;

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
    ItemType getItemType();

    /**
     * Returns the amount that this item stack represents.
     * @return the amount this item stack represents
     */
    int getAmount();

    /**
     * Returns the item data that this item has.
     * @return the item data
     */
    Optional<Metadata> getItemData();

    /**
     * Creates a builder from this item stack.
     * @return a {@link ItemStackBuilder}
     */
    ItemStackBuilder toBuilder();

    /**
     * Determines whether or not this item stack is similar to {@code other}.
     * @param other the other item stack to check
     * @return whether or not the stacks are similar
     */
    boolean isSimiliarTo(@Nonnull ItemStack other);
}
