package com.voxelwind.server.game.level.provider.anvil.util;

import com.flowpowered.nbt.*;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.server.game.item.VoxelwindItemStack;

public class VoxelwindNBTUtils {
    public static ItemStack createItemStack(CompoundMap map) {
        ByteTag countTag = (ByteTag) map.get("Count");
        ShortTag damageTag = (ShortTag) map.get("Damage");
        ShortTag idTag = (ShortTag) map.get("id");

        // TODO: "tag" compound
        ItemType type = ItemTypes.forId(idTag.getValue());
        return new VoxelwindItemStack(type, countTag.getValue(), type.createDataFor(damageTag.getValue()).orElse(null));
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
