package com.voxelwind.server.game.inventories;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.voxelwind.api.game.inventories.Inventory;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.server.Player;
import com.voxelwind.server.network.session.PlayerSession;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class VoxelwindBaseInventory implements Inventory {
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
        setItem(slot, stack, null);
    }

    public void setItem(int slot, @Nonnull ItemStack stack, PlayerSession session) {
        Preconditions.checkNotNull(stack, "stack");
        Preconditions.checkArgument(slot >= 0 && slot < fullSize, "Wanted slot %s is not between 0 and %s", slot, fullSize);
        if (!isNothing(stack)) {
            ItemStack oldItem = inventory.put(slot, stack);
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
        for (int i = 0; i < fullSize; i++) {
            if (!inventory.containsKey(i)) {
                inventory.put(i, stack);
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
        Preconditions.checkArgument(slot >= 0 && slot < fullSize, "Wanted slot %s is not between 0 and %s", slot, fullSize);
        ItemStack stack = inventory.remove(slot);
        if (stack != null) {
            for (InventoryObserver observer : observerList) {
                observer.onInventoryChange(slot, stack, null, this, session);
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
        for (InventoryObserver observer : observerList) {
            observer.onInventoryContentsReplacement(ImmutableMap.of(), this);
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
        inventory.clear();
        inventory.putAll(contentsCopy);
        for (InventoryObserver observer : observerList) {
            observer.onInventoryContentsReplacement(contentsCopy, this);
        }
    }

    private static boolean isNothing(ItemStack stack) {
        return stack == null || stack.getItemType() == BlockTypes.AIR;
    }

    public List<InventoryObserver> getObserverList() {
        return observerList;
    }
}
