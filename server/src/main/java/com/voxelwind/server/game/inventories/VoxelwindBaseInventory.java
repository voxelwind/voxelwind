package com.voxelwind.server.game.inventories;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.voxelwind.api.game.inventories.Inventory;
import com.voxelwind.api.game.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class VoxelwindBaseInventory implements Inventory {
    private final Map<Integer, ItemStack> inventory = new HashMap<>();
    private final List<InventoryObserver> observerList = new CopyOnWriteArrayList<>();
    private final int fullSize;

    protected VoxelwindBaseInventory(int fullSize) {
        this.fullSize = fullSize;
    }

    @Override
    public Optional<ItemStack> getItem(int slot) {
        Preconditions.checkArgument(slot >= 0 && slot < fullSize, "Wanted slot %s is not between 0 and %s", slot, fullSize);
        return Optional.ofNullable(inventory.get(slot));
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack) {
        Preconditions.checkNotNull(stack, "stack");
        Preconditions.checkArgument(slot >= 0 && slot < fullSize, "Wanted slot %s is not between 0 and %s", slot, fullSize);
        ItemStack oldItem = inventory.put(slot, stack);
        for (InventoryObserver observer : observerList) {
            observer.onInventoryChange(slot, oldItem, stack, null);
        }
    }

    @Override
    public boolean addItem(@Nonnull ItemStack stack) {
        Preconditions.checkNotNull(stack, "stack");
        for (int i = 0; i < fullSize; i++) {
            if (!inventory.containsKey(i)) {
                inventory.put(i, stack);
                for (InventoryObserver observer : observerList) {
                    observer.onInventoryChange(i, null, stack, null);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearItem(int slot) {
        Preconditions.checkArgument(slot >= 0 && slot < fullSize, "Wanted slot %s is not between 0 and %s", slot, fullSize);
        ItemStack stack = inventory.remove(slot);
        if (stack != null) {
            for (InventoryObserver observer : observerList) {
                observer.onInventoryChange(slot, stack, null, null);
            }
        }
    }

    @Override
    public int getInventorySize() {
        return fullSize;
    }

    @Override
    public int getUsableInventorySize() {
        return fullSize;
    }

    @Override
    public void clearAll() {
        for (Map.Entry<Integer, ItemStack> entry : inventory.entrySet()) {
            for (InventoryObserver observer : observerList) {
                observer.onInventoryChange(entry.getKey(), entry.getValue(), null, null);
            }
        }
        inventory.clear();
    }

    @Override
    public Map<Integer, ItemStack> getAllContents() {
        return ImmutableMap.copyOf(inventory);
    }

    @Override
    public void setAllContents(@Nonnull Map<Integer, ItemStack> contents) {
        Preconditions.checkNotNull(contents, "contents");
        Map<Integer, ItemStack> contentsCopy = ImmutableMap.copyOf(contents);
        if (contentsCopy.isEmpty()) {
            clearAll();
            return;
        }

        Integer maxSlot = Collections.max(contentsCopy.keySet());
        Preconditions.checkArgument(maxSlot < fullSize, "Maximum passed contents slot (%s) is greater than this inventory's size (%s)",
                maxSlot, fullSize);

        Set<Integer> slotsRemoved = new HashSet<>(inventory.keySet());
        slotsRemoved.removeAll(contentsCopy.keySet());

        for (Integer integer : slotsRemoved) {
            for (InventoryObserver observer : observerList) {
                observer.onInventoryChange(integer, inventory.get(integer), null, null);
            }
        }
        inventory.keySet().removeAll(slotsRemoved);

        for (Map.Entry<Integer, ItemStack> entry : contentsCopy.entrySet()) {
            ItemStack previous = inventory.put(entry.getKey(), entry.getValue());
            for (InventoryObserver observer : observerList) {
                observer.onInventoryChange(entry.getKey(), previous, entry.getValue(), null);
            }
        }
    }
}
