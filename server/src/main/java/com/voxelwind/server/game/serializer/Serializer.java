package com.voxelwind.server.game.serializer;

import com.flowpowered.nbt.CompoundTag;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.blockentities.BlockEntity;

import java.util.Optional;

public interface Serializer {
    CompoundTag readNBT(BlockState block);
    short readMetadata(BlockState block);
    CompoundTag readNBT(ItemStack itemStack);
    short readMetadata(ItemStack itemStack);
    Metadata writeMetadata(ItemType block, short metadata);
    BlockEntity writeNBT(ItemType block, CompoundTag nbtTag);

    default <T> T getItemData(ItemStack itemStack) {
        Optional<Metadata> optional = itemStack.getItemData();
        return optional.isPresent() ? (T) optional.get() : null;
    }

    default <T> T getBlockData(BlockState block) {
        return (T) block.getBlockData();
    }

    default <T> T getBlockStateEntity(BlockState block) {
        Optional<BlockEntity> optional = block.getBlockEntity();
        return optional.isPresent() ? (T) optional.get() : null;
    }
}
