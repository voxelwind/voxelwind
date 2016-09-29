package com.voxelwind.server.game.serializer;

import com.flowpowered.nbt.CompoundTag;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.data.GenericDamageValue;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.blockentities.BlockEntity;

public class GenericDamageSerializer implements Serializer {
    @Override
    public CompoundTag readNBT(Block block) {
        return null;
    }

    @Override
    public short readMetadata(BlockState block) {
        return 0;
    }

    @Override
    public CompoundTag readNBT(ItemStack itemStack) {
        return null;
    }

    @Override
    public short readMetadata(ItemStack itemStack) {
        GenericDamageValue data = getItemData(itemStack);
        return data != null ? data.getDamage() : 0;
    }

    @Override
    public Metadata writeMetadata(ItemType block, short metadata) {
        return new GenericDamageValue(metadata);
    }

    @Override
    public BlockEntity writeNBT(ItemType block, CompoundTag nbtTag) {
        return null;
    }
}
