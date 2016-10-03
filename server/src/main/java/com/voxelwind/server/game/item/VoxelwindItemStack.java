package com.voxelwind.server.game.item;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.server.game.serializer.MetadataSerializer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode
@ToString
public class VoxelwindItemStack implements ItemStack {
    private final ItemType itemType;
    private final int amount;
    private final Metadata data;
    private final String itemName;

    public VoxelwindItemStack(ItemType itemType, int amount, Metadata data) {
        this(itemType, amount, data, null);
    }

    public VoxelwindItemStack(ItemType itemType, int amount, Metadata data, String itemName) {
        this.itemType = itemType;
        this.amount = amount;
        this.data = data;
        this.itemName = itemName;
    }

    public ItemType getItemType() {
        return itemType;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public Optional<Metadata> getItemData() {
        return Optional.ofNullable(data);
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(itemName);
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

    public CompoundTag toFullNBT() {
        CompoundMap map = new CompoundMap();
        map.put(new ByteTag("Count", (byte) amount));
        map.put(new ShortTag("Damage", MetadataSerializer.serializeMetadata(this)));
        map.put(new ShortTag("id", (short) itemType.getId()));
        map.put(new CompoundTag("tag", toSpecificNBT().getValue()));
        return new CompoundTag("", map);
    }

    public CompoundTag toSpecificNBT() {
        CompoundMap map = new CompoundMap();

        // Display properties
        if (itemName != null) {
            CompoundMap displayMap = new CompoundMap();
            if (itemName != null) {
                displayMap.put(new StringTag("Name", itemName));
            }

            map.put(new CompoundTag("display", displayMap));
        }

        return new CompoundTag("", map);
    }
}
