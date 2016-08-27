package com.voxelwind.server.game.inventories;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.inventories.Inventory;
import com.voxelwind.api.game.inventories.InventoryType;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.server.network.session.PlayerSession;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class VoxelwindBaseInventory implements Inventory {
    private final ItemStack[] inventory;
    private final List<InventoryObserver> observerList = new CopyOnWriteArrayList<>();
    private final InventoryType type;

    protected VoxelwindBaseInventory(InventoryType type) {
        this.type = type;
        this.inventory = new ItemStack[type.getInventorySize()];
    }

    @Override
    public Optional<ItemStack> getItem(int slot) {
        Preconditions.checkArgument(slot >= 0 && slot < type.getInventorySize(), "Wanted slot %s is not between 0 and %s", slot, type.getInventorySize());
        return Optional.ofNullable(inventory[slot]);
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack) {
        setItem(slot, stack, null);
    }

    public void setItem(int slot, @Nonnull ItemStack stack, PlayerSession session) {
        Preconditions.checkNotNull(stack, "stack");
        Preconditions.checkArgument(slot >= 0 && slot < type.getInventorySize(), "Wanted slot %s is not between 0 and %s", slot, type.getInventorySize());
        if (!isNothing(stack)) {
            ItemStack oldItem = inventory[slot];
            inventory[slot] = stack;
            for (InventoryObserver observer : observerList) {
                observer.onInventoryChange(slot, oldItem, stack, this, session);
            }
        } else {
            clearItem(slot);
        }
    }

    @Override
    public boolean addItem(@Nonnull ItemStack stack) {
        return addItem(stack, null);
    }

    public boolean addItem(@Nonnull ItemStack stack, PlayerSession session) {
        Preconditions.checkNotNull(stack, "stack");
        for (int i = 0; i < type.getInventorySize(); i++) {
            // TODO: Add smart stacking!
            if (inventory[i] == null) {
                inventory[i] = stack;
                for (InventoryObserver observer : observerList) {
                    observer.onInventoryChange(i, null, stack, this, session);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearItem(int slot) {
        clearItem(slot, null);
    }

    public void clearItem(int slot, PlayerSession session) {
        Preconditions.checkArgument(slot >= 0 && slot < type.getInventorySize(), "Wanted slot %s is not between 0 and %s", slot, type.getInventorySize());
        ItemStack stack = inventory[slot];
        if (stack != null) {
            inventory[slot] = null;
            for (InventoryObserver observer : observerList) {
                observer.onInventoryChange(slot, stack, null, this, session);
            }
        }
    }

    @Override
    public int getUsableInventorySize() {
        return type.getInventorySize();
    }

    @Override
    public void clearAll() {
        Arrays.fill(inventory, null);
        for (InventoryObserver observer : observerList) {
            observer.onInventoryContentsReplacement(inventory, this);
        }
    }

    @Override
    public ItemStack[] getAllContents() {
        return Arrays.copyOf(inventory, inventory.length);
    }

    @Override
    public void setAllContents(@Nonnull ItemStack[] contents) {
        Preconditions.checkNotNull(contents, "contents");
        Preconditions.checkArgument(contents.length == type.getInventorySize(), "Passed contents size %s is not equal to this inventory's size (%s)",
                contents.length, type.getInventorySize());
        System.arraycopy(contents, 0, inventory, 0, inventory.length);
        for (InventoryObserver observer : observerList) {
            observer.onInventoryContentsReplacement(contents, this);
        }
    }

    @Override
    public InventoryType getInventoryType() {
        return type;
    }

    private static boolean isNothing(ItemStack stack) {
        return stack == null || stack.getItemType() == BlockTypes.AIR;
    }

    public List<InventoryObserver> getObserverList() {
        return observerList;
    }
}
