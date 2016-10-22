package com.voxelwind.server.game.serializer;

import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.data.Crops;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.nbt.tags.CompoundTag;

public class CropsSerializer implements Serializer {
    @Override
    public CompoundTag readNBT(BlockState block) {
        return null;
    }

    @Override
    public short readMetadata(BlockState block) {
        Crops data = getBlockData(block);
        return (short) (data != null ? data.getLevel() : 0);
    }

    @Override
    public CompoundTag readNBT(ItemStack itemStack) {
        return null;
    }

    @Override
    public short readMetadata(ItemStack itemStack) {
        return 0;
    }

    @Override
    public Metadata writeMetadata(ItemType block, short metadata) {
        return Crops.of(metadata);
    }

    @Override
    public BlockEntity writeNBT(ItemType block, CompoundTag nbtTag) {
        return null;
    }
}
