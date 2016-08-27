package com.voxelwind.api.game.inventories;

import com.voxelwind.api.game.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Optional;

/**
 * Defines an inventory. Inventories are always backed by a holder, typically a block entity or player.
 */
@ParametersAreNonnullByDefault
public interface Inventory {
    /**
     * Returns the item in the specified slot. This may be null.
     * @param slot the slot where the item is
     * @return an {@link Optional} with the item
     */
    Optional<ItemStack> getItem(int slot);

    /**
     * Sets the item in the slot to the desired item.
     * @param slot the slot to set
     * @param stack the item to set in the slot
     */
    void setItem(int slot, ItemStack stack);

    /**
     * Adds an item to the inventory.
     * @param stack the item to add
     * @return whether or not the item could be added successfully
     */
    boolean addItem(ItemStack stack);

    /**
     * Clears the item in the inventory at the specified slot.
     * @param slot the slot to clear
     */
    void clearItem(int slot);

    /**
     * Returns the full size of this inventory.
     * @return the full size of this inventory
     */
    int getInventorySize();

    /**
     * Returns the usable size of this inventory. For instance, armor item slots would not be considered "usable".
     * @return the usable size of this inventory
     */
    int getUsableInventorySize();

    /**
     * Clears all items from the inventory.
     */
    void clearAll();

    /**
     * Returns all the items in this inventory. The returned map will be immutable.
     * @return all the items in this inventory
     */
    Map<Integer, ItemStack> getAllContents();

    /**
     * Sets the contents of the entire inventory.
     * @param contents the inventory contents
     */
    void setAllContents(Map<Integer, ItemStack> contents);
}
