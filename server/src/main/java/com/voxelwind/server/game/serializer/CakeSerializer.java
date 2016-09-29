package com.voxelwind.server.game.serializer;

import com.flowpowered.nbt.CompoundTag;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.data.Cake;
import com.voxelwind.api.game.level.blockentities.BlockEntity;

public class CakeSerializer implements Serializer {
    @Override
    public CompoundTag readNBT(Block block) {
        return null;
    }

    @Override
    public short readMetadata(BlockState block) {
        Cake data = getBlockData(block);
        return (short) (data != null ? data.getSlicesEaten() : 0);
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
        return Cake.of(metadata);
    }

    @Override
    public BlockEntity writeNBT(ItemType block, CompoundTag nbtTag) {
        return null;
    }
}
