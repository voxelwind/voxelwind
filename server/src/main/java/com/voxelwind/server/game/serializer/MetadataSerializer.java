package com.voxelwind.server.game.serializer;

import com.flowpowered.nbt.CompoundTag;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.level.block.Block;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import gnu.trove.map.hash.TIntObjectHashMap;

public class MetadataSerializer implements Serializer {
    private static final TIntObjectHashMap<Serializer> SERIALIZERS = new TIntObjectHashMap<>();
    private static final MetadataSerializer INSTANCE = new MetadataSerializer();

    static {
        // All blocks which need this
        SERIALIZERS.put(BlockTypes.CAKE.getId(), new CakeSerializer());
        SERIALIZERS.put(BlockTypes.CROPS.getId(), new CropsSerializer());
        SERIALIZERS.put(BlockTypes.FLOWER_POT.getId(), new FlowerpotSerializer());
        SERIALIZERS.put(BlockTypes.WOOL.getId(), new DyedSerializer());

        // All additional items which need this
        SERIALIZERS.put(ItemTypes.COAL.getId(), new CoalSerializer());
        SERIALIZERS.put(ItemTypes.DYE.getId(), new DyedSerializer());
    }

    private MetadataSerializer() {

    }

    @Override
    public CompoundTag readNBT(Block block) {
        Serializer dataSerializer = SERIALIZERS.get(block.getBlockState().getBlockType().getId());
        if (dataSerializer == null) {
            return null;
        }

        return dataSerializer.readNBT(block);
    }

    @Override
    public short readMetadata(BlockState block) {
        Serializer dataSerializer = SERIALIZERS.get(block.getBlockType().getId());
        if (dataSerializer == null) {
            return 0;
        }

        return dataSerializer.readMetadata(block);
    }

    @Override
    public CompoundTag readNBT(ItemStack itemStack) {
        Serializer dataSerializer = SERIALIZERS.get(itemStack.getItemType().getId());
        if (dataSerializer == null) {
            return null;
        }

        return dataSerializer.readNBT(itemStack);
    }

    @Override
    public short readMetadata(ItemStack itemStack) {
        Serializer dataSerializer = SERIALIZERS.get(itemStack.getItemType().getId());
        if (dataSerializer == null) {
            return 0;
        }

        return dataSerializer.readMetadata(itemStack);
    }

    @Override
    public Metadata writeMetadata(ItemType item, short metadata) {
        Serializer dataSerializer = SERIALIZERS.get(item.getId());
        if (dataSerializer == null) {
            return null;
        }

        return dataSerializer.writeMetadata(item, metadata);
    }

    @Override
    public BlockEntity writeNBT(ItemType item, CompoundTag nbtTag) {
        Serializer dataSerializer = SERIALIZERS.get(item.getId());
        if (dataSerializer == null) {
            return null;
        }

        return dataSerializer.writeNBT(item, nbtTag);
    }

    public static Metadata deserializeMetadata(ItemType type, short metadata) {
        return INSTANCE.writeMetadata(type, metadata);
    }

    public static short serializeMetadata(BlockState block) {
        return INSTANCE.readMetadata(block);
    }

    public static short serializeMetadata(ItemStack itemStack) {
        return INSTANCE.readMetadata(itemStack);
    }

    public static CompoundTag serializeNBT(Block block) {
        return INSTANCE.readNBT(block);
    }
}
