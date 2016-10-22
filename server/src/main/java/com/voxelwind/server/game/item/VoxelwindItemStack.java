package com.voxelwind.server.game.item;

import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.nbt.tags.*;
import com.voxelwind.server.game.serializer.MetadataSerializer;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
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
        List<Tag<?>> tags = new ArrayList<>();
        tags.add(new ByteTag("Count", (byte) amount));
        tags.add(new ShortTag("Damage", MetadataSerializer.serializeMetadata(this)));
        tags.add(new ShortTag("id", (short) itemType.getId()));
        tags.add(new CompoundTag("tag", toSpecificNBT().getValue()));
        return CompoundTag.createFromList("", tags);
    }

    public CompoundTag toSpecificNBT() {
        List<Tag<?>> tags = new ArrayList<>();

        // Display properties
        if (itemName != null) {
            List<Tag<?>> displayTags = new ArrayList<>();
            if (itemName != null) {
                displayTags.add(new StringTag("Name", itemName));
            }

            tags.add(CompoundTag.createFromList("display", displayTags));
        }

        return CompoundTag.createFromList("", tags);
    }
}
