package com.voxelwind.server.game.inventories;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.voxelwind.api.game.inventories.Inventory;
import com.voxelwind.api.game.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class VoxelwindBaseInventory implements Inventory {
    private final Map<Integer, ItemStack> inventory = new HashMap<>();
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
        inventory.put(slot, stack);
    }

    @Override
    public boolean addItem(@Nonnull ItemStack stack) {
        Preconditions.checkNotNull(stack, "stack");
        for (int i = 0; i < fullSize; i++) {
            if (!inventory.containsKey(i)) {
                inventory.put(i, stack);
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearItem(int slot) {
        Preconditions.checkArgument(slot >= 0 && slot < fullSize, "Wanted slot %s is not between 0 and %s", slot, fullSize);
        inventory.remove(slot);
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
        inventory.clear();
    }

    @Override
    public Map<Integer, ItemStack> getAllContents() {
        return ImmutableMap.copyOf(inventory);
    }

    @Override
    public void setAllContents(@Nonnull Map<Integer, ItemStack> contents) {
        Preconditions.checkNotNull(contents, "contents");
        inventory.clear();
        inventory.putAll(ImmutableMap.copyOf(contents));
    }
}
