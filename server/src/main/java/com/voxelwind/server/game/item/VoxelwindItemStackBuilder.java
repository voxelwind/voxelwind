package com.voxelwind.server.game.item;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.ItemType;

import javax.annotation.Nonnull;

public class VoxelwindItemStackBuilder implements ItemStackBuilder {
    private ItemType itemType;
    private int amount = 1;
    private Metadata data;

    @Override
    public ItemStackBuilder itemType(@Nonnull ItemType itemType) {
        Preconditions.checkNotNull(itemType, "itemType");
        this.itemType = itemType;
        this.data = null; // No data
        return this;
    }

    @Override
    public ItemStackBuilder amount(int amount) {
        Preconditions.checkState(itemType != null, "ItemType has not been set");
        Preconditions.checkArgument(amount >= 0 && amount <= itemType.getMaximumStackSize(), "Amount %s is not between 0 and %s", amount, itemType.getMaximumStackSize());
        this.amount = amount;
        return this;
    }

    @Override
    public ItemStackBuilder itemData(Metadata data) {
        if (data != null) {
            Preconditions.checkState(itemType != null, "ItemType has not been set");
            Preconditions.checkArgument(itemType.getMetadataClass() != null, "Item does not have any data associated with it.");
            Preconditions.checkArgument(data.getClass().isAssignableFrom(itemType.getMetadataClass()), "ItemType data is not valid (wanted %s)",
                    itemType.getMetadataClass().getName());
        }
        this.data = data;
        return this;
    }

    @Override
    public ItemStack build() {
        Preconditions.checkArgument(itemType != null, "ItemType has not been set");
        return new VoxelwindItemStack(itemType, amount, data);
    }

    @Override
    public String toString() {
        return "VoxelwindItemStackBuilder{" +
                "itemType=" + itemType +
                ", amount=" + amount +
                ", data=" + data +
                '}';
    }
}
