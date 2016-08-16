package com.voxelwind.server.game.item;

import java.util.Objects;

public class ItemStack {
    private final ItemType itemType;
    private final int amount;

    public ItemStack(ItemType itemType, int amount) {
        this.itemType = itemType;
        this.amount = amount;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemStack itemStack = (ItemStack) o;
        return amount == itemStack.amount &&
                itemType == itemStack.itemType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemType, amount);
    }

    @Override
    public String toString() {
        return "ItemStack{" +
                "itemType=" + itemType +
                ", amount=" + amount +
                '}';
    }
}
