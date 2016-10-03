package com.voxelwind.server.game.item;

import com.flowpowered.nbt.*;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import com.voxelwind.server.game.serializer.MetadataSerializer;

public class VoxelwindNBTUtils {
    public static ItemStack createItemStack(CompoundMap map) {
        ByteTag countTag = (ByteTag) map.get("Count");
        ShortTag damageTag = (ShortTag) map.get("Damage");
        ShortTag idTag = (ShortTag) map.get("id");
        ItemType type = ItemTypes.forId(idTag.getValue());

        ItemStackBuilder builder = new VoxelwindItemStackBuilder()
                .itemType(type)
                .amount(countTag.getValue())
                .itemData(MetadataSerializer.deserializeMetadata(type, damageTag.getValue()));

        Tag<?> tagTag = map.get("tag");
        if (tagTag != null) {
            applyItemData(builder, (CompoundMap) tagTag.getValue());
        }

        return builder.build();
    }

    public static void applyItemData(ItemStackBuilder builder, CompoundMap map) {
        if (map.containsKey("display")) {
            CompoundMap displayTag = ((CompoundTag) map.get("display")).getValue();
            if (displayTag.containsKey("Name")) {
                builder.name((String) displayTag.get("Name").getValue());
            }
        }
    }

    public static ItemStack[] createItemStacks(ListTag<CompoundTag> tag, int knownSize) {
        ItemStack[] all = new ItemStack[knownSize];
        for (CompoundTag slotTag : tag.getValue()) {
            CompoundMap slotMap = slotTag.getValue();
            Tag<?> inSlotTagRaw = slotMap.get("Slot");
            Preconditions.checkArgument(inSlotTagRaw != null, "Slot NBT tag is missing from compound");
            Preconditions.checkArgument(inSlotTagRaw instanceof ByteTag, "Slot NBT tag is not a Byte");
            ByteTag inSlotTag = (ByteTag) inSlotTagRaw;
            if (inSlotTag.getValue() < 0 || inSlotTag.getValue() >= knownSize) {
                throw new IllegalArgumentException("Found illegal slot " + inSlotTag.getValue());
            }
            all[inSlotTag.getValue()] = createItemStack(slotMap);
        }
        return all;
    }
}
