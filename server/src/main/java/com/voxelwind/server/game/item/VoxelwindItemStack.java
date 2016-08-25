package com.voxelwind.server.game.item;

import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.data.ItemData;

import java.io.IOException;
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

    public void writeNbt(NBTOutputStream stream) throws IOException {
        // TODO: Implement
    }

    public void readNbt(NBTInputStream stream) throws IOException {
        // TODO: Implement
    }
}
