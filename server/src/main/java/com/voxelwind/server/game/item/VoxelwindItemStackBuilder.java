package com.voxelwind.server.game.item;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.data.ItemData;

import javax.annotation.Nonnull;

public class VoxelwindItemStackBuilder implements ItemStackBuilder {
    private ItemType itemType;
    private int amount = 1;
    private ItemData data;

    @Override
    public ItemStackBuilder item(@Nonnull ItemType itemType) {
        Preconditions.checkNotNull(itemType, "itemType");
        this.itemType = itemType;
        this.data = null; // No data
        return this;
    }

    @Override
    public ItemStackBuilder amount(int amount) {
        Preconditions.checkArgument(amount >= 0 && amount <= 64, "Amount %s is not between 0 and 64", amount);
        this.amount = amount;
        return this;
    }

    @Override
    public ItemStackBuilder itemData(ItemData data) {
        if (data != null) {
            Preconditions.checkState(itemType != null, "ItemType has not been set");
            Preconditions.checkArgument(itemType.getMaterialDataClass() != null, "Item does not have any data associated with it.");
            Preconditions.checkArgument(data.getClass().isAssignableFrom(itemType.getMaterialDataClass()), "ItemType data is not valid (wanted %s)",
                    itemType.getMaterialDataClass().getName());
        }
        this.data = data;
        return this;
    }

    @Override
    public ItemStack build() {
        Preconditions.checkArgument(itemType != null, "ItemType has not been set");
        return new VoxelwindItemStack(itemType, amount, data);
    }
}
