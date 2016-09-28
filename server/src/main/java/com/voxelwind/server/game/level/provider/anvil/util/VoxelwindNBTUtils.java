package com.voxelwind.server.game.level.provider.anvil.util;

import com.flowpowered.nbt.ByteTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.ShortTag;
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
}
