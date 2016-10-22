package com.voxelwind.server.game.item;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemStackBuilder;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.nbt.tags.*;
import com.voxelwind.server.game.serializer.MetadataSerializer;

import java.util.Map;

public class VoxelwindNBTUtils {
    public static ItemStack createItemStack(Map<String, Tag<?>> map) {
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
            applyItemData(builder, (Map<String, Tag<?>>) tagTag.getValue());
        }

        return builder.build();
    }

    public static void applyItemData(ItemStackBuilder builder, Map<String, Tag<?>> map) {
        if (map.containsKey("display")) {
            Map<String, Tag<?>> displayTag = ((CompoundTag) map.get("display")).getValue();
            if (displayTag.containsKey("Name")) {
                builder.name((String) displayTag.get("Name").getValue());
            }
        }
    }

    public static ItemStack[] createItemStacks(ListTag<CompoundTag> tag, int knownSize) {
        ItemStack[] all = new ItemStack[knownSize];
        for (CompoundTag slotTag : tag.getValue()) {
            Map<String, Tag<?>> slotMap = slotTag.getValue();
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
