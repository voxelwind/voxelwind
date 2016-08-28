package com.voxelwind.server.game.item;

import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.data.ItemData;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;
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
        return new VoxelwindItemStackBuilder().itemType(itemType).amount(amount).itemData(data);
    }

    @Override
    public boolean isSimiliarTo(@Nonnull ItemStack other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        VoxelwindItemStack stack = (VoxelwindItemStack) other;
        return Objects.equals(itemType, stack.itemType) &&
                Objects.equals(data, stack.data);
    }

    public void writeNbt(NBTOutputStream stream) throws IOException {
        // TODO: Implement
    }

    public void readNbt(NBTInputStream stream) throws IOException {
        // TODO: Implement
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoxelwindItemStack stack = (VoxelwindItemStack) o;
        return amount == stack.amount &&
                Objects.equals(itemType, stack.itemType) &&
                Objects.equals(data, stack.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemType, amount, data);
    }

    @Override
    public String toString() {
        return "VoxelwindItemStack{" +
                "itemType=" + itemType +
                ", amount=" + amount +
                ", data=" + data +
                '}';
    }
}
