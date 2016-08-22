package com.voxelwind.server.game.item;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.data.ItemData;

import java.util.Optional;

public class VoxelwindItemStack implements ItemStack {
    private final ItemType itemType;
    private final int amount;
    private final ItemData data;

    public VoxelwindItemStack(ItemType itemType, int amount, ItemData data) {
        this.itemType = itemType;
        this.amount = amount;
        this.data = data;
    }

    public ItemType getItemType() {
        return itemType;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public Optional<ItemData> getItemData() {
        return Optional.ofNullable(data);
    }

    @Override
    public ItemStackBuilder toBuilder() {
        return new VoxelwindItemStackBuilder().material(itemType).amount(amount).itemData(data);
    }
}
