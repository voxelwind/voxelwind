package com.voxelwind.server.game.serializer;

import com.flowpowered.nbt.CompoundTag;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.data.Coal;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.blockentities.BlockEntity;

public class CoalSerializer implements Serializer {
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
        Coal coal = getItemData(itemStack);
        return (short) ( coal != null ? ( coal.isCharcoal() ? 1 : 0 ) : 0 );
    }

    @Override
    public Metadata writeMetadata(ItemType block, short metadata) {
        return (metadata == 0) ? Coal.REGULAR : Coal.CHARCOAL;
    }

    @Override
    public BlockEntity writeNBT(ItemType block, CompoundTag nbtTag) {
        return null;
    }
}
