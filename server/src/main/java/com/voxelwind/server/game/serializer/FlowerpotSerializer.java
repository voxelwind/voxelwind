package com.voxelwind.server.game.serializer;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.FlowerType;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.nbt.tags.CompoundTag;
import com.voxelwind.nbt.tags.StringTag;
import com.voxelwind.server.game.level.blockentities.VoxelwindFlowerpotBlockEntity;

public class FlowerpotSerializer implements Serializer {
    @Override
    public CompoundTag readNBT(BlockState block) {
        VoxelwindFlowerpotBlockEntity flowerPot = getBlockStateEntity(block);
        return CompoundTag.createFromList("", ImmutableList.of(new StringTag("contents", flowerPot.getFlowerType().name().toLowerCase())));
    }

    @Override
    public short readMetadata(BlockState block) {
        return 0;
    }

    @Override
    public CompoundTag readNBT(ItemStack itemStack) {
        VoxelwindFlowerpotBlockEntity flowerPot = getItemData(itemStack);
        return CompoundTag.createFromList("", ImmutableList.of(new StringTag("contents", flowerPot.getFlowerType().name().toLowerCase())));
    }

    @Override
    public short readMetadata(ItemStack itemStack) {
        return 0;
    }

    @Override
    public Metadata writeMetadata(ItemType block, short metadata) {
        return null;
    }

    @Override
    public BlockEntity writeNBT(ItemType block, CompoundTag nbtTag) {
        FlowerType flowerType = FlowerType.valueOf(((String) nbtTag.getValue().get("contents").getValue()).toUpperCase());
        return new VoxelwindFlowerpotBlockEntity(flowerType);
    }
}
