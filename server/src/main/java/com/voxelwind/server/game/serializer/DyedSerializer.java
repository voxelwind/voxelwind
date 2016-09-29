package com.voxelwind.server.game.serializer;

import com.flowpowered.nbt.CompoundTag;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.data.Dyed;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.api.util.DyeColor;

public class DyedSerializer implements Serializer {
    @Override
    public CompoundTag readNBT(BlockState block) {
        return null;
    }

    @Override
    public short readMetadata(BlockState block) {
        Dyed data = getBlockData(block);
        return (short) (data != null ? data.getColor().ordinal() : 0);
    }

    @Override
    public CompoundTag readNBT(ItemStack itemStack) {
        return null;
    }

    @Override
    public short readMetadata(ItemStack itemStack) {
        Dyed data = getItemData(itemStack);
        return (short) (data != null ? data.getColor().ordinal() : 0);
    }

    @Override
    public Metadata writeMetadata(ItemType block, short metadata) {
        DyeColor[] colors = DyeColor.values();
        Preconditions.checkArgument(metadata >= 0 && metadata < colors.length, "color is not valid");
        return Dyed.of(colors[metadata]);
    }

    @Override
    public BlockEntity writeNBT(ItemType block, CompoundTag nbtTag) {
        return null;
    }
}
