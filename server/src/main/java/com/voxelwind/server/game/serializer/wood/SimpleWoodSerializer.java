package com.voxelwind.server.game.serializer.wood;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.data.wood.Wood;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.api.game.util.data.TreeSpecies;
import com.voxelwind.nbt.tags.CompoundTag;
import com.voxelwind.server.game.serializer.Serializer;

import java.util.Arrays;

public class SimpleWoodSerializer implements Serializer {
    private static final TreeSpecies[] ALL = TreeSpecies.values();

    @Override
    public CompoundTag readNBT(BlockState block) {
        return null;
    }

    @Override
    public short readMetadata(BlockState block) {
        Wood wood = getBlockData(block);
        if (wood != null) {
            int result = Arrays.binarySearch(ALL, wood.getSpecies(), null);
            if (result >= 0) {
                return (short) result;
            }
        }

        return 0;
    }

    @Override
    public CompoundTag readNBT(ItemStack itemStack) {
        return null;
    }

    @Override
    public short readMetadata(ItemStack itemStack) {
        Wood wood = getItemData(itemStack);
        if (wood != null) {
            int result = Arrays.binarySearch(ALL, wood.getSpecies(), null);
            if (result >= 0) {
                return (short) result;
            }
        }

        return 0;
    }

    @Override
    public Metadata writeMetadata(ItemType block, short metadata) {
        Preconditions.checkArgument(metadata >= 0 && metadata < ALL.length, "metadata value %s not between 0 and %s", metadata, ALL.length - 1);
        return Wood.of(ALL[metadata]);
    }

    @Override
    public BlockEntity writeNBT(ItemType block, CompoundTag nbtTag) {
        return null;
    }
}
