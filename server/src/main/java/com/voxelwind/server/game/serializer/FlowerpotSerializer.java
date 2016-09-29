package com.voxelwind.server.game.serializer;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.StringTag;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.FlowerType;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.server.game.level.blockentities.VoxelwindFlowerpotBlockEntity;

public class FlowerpotSerializer implements Serializer {
    @Override
    public CompoundTag readNBT(BlockState block) {
        VoxelwindFlowerpotBlockEntity flowerPot = getBlockStateEntity(block);

        CompoundMap map = new CompoundMap();
        map.put("contents", new StringTag("contents", flowerPot.getFlowerType().name().toLowerCase()));
        return new CompoundTag("", map);
    }

    @Override
    public short readMetadata(BlockState block) {
        return 0;
    }

    @Override
    public CompoundTag readNBT(ItemStack itemStack) {
        VoxelwindFlowerpotBlockEntity flowerPot = getItemData(itemStack);

        CompoundMap map = new CompoundMap();
        map.put("contents", new StringTag("contents", flowerPot.getFlowerType().name().toLowerCase()));
        return new CompoundTag("", map);
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
