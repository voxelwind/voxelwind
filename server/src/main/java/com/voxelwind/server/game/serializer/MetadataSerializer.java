package com.voxelwind.server.game.serializer;

import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.level.blockentities.BlockEntity;
import com.voxelwind.nbt.tags.CompoundTag;
import com.voxelwind.server.game.serializer.wood.LogSerializer;
import com.voxelwind.server.game.serializer.wood.SimpleWoodSerializer;
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
        SERIALIZERS.put(BlockTypes.WOOD.getId(), new LogSerializer());
        SERIALIZERS.put(BlockTypes.ACACIA_WOOD.getId(), new LogSerializer());
        SERIALIZERS.put(BlockTypes.WOOD_PLANKS.getId(), new SimpleWoodSerializer());

        // All additional items which need this
        SERIALIZERS.put(ItemTypes.COAL.getId(), new CoalSerializer());
        SERIALIZERS.put(ItemTypes.DYE.getId(), new DyedSerializer());
        SERIALIZERS.put(ItemTypes.IRON_SHOVEL.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.IRON_PICKAXE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.IRON_AXE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.FLINT_AND_STEEL.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.WOODEN_SWORD.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.WOODEN_SHOVEL.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.WOODEN_PICKAXE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.WOODEN_AXE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.STONE_SWORD.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.STONE_SHOVEL.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.STONE_PICKAXE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.STONE_AXE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.DIAMOND_SWORD.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.DIAMOND_SHOVEL.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.DIAMOND_PICKAXE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.DIAMOND_AXE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.GOLDEN_SWORD.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.GOLDEN_SHOVEL.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.GOLDEN_PICKAXE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.GOLDEN_AXE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.WOODEN_HOE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.STONE_HOE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.IRON_HOE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.DIAMOND_HOE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.GOLDEN_HOE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.LEATHER_CAP.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.LEATHER_TUNIC.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.LEATHER_PANTS.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.LEATHER_BOOTS.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.CHAIN_HELMET.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.CHAIN_CHESTPLATE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.CHAIN_LEGGINGS.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.CHAIN_BOOTS.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.IRON_HELMET.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.IRON_CHESTPLATE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.IRON_LEGGINGS.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.IRON_BOOTS.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.DIAMOND_HELMET.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.DIAMOND_CHESTPLATE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.DIAMOND_LEGGINGS.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.DIAMOND_BOOTS.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.GOLDEN_HELMET.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.GOLDEN_CHESTPLATE.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.GOLDEN_LEGGINGS.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.GOLDEN_BOOTS.getId(), new GenericDamageSerializer());
        SERIALIZERS.put(ItemTypes.BOW.getId(), new GenericDamageSerializer());
    }

    private MetadataSerializer() {

    }

    public static Metadata deserializeMetadata(ItemType type, short metadata) {
        return INSTANCE.writeMetadata(type, metadata);
    }

    public static BlockEntity deserializeNBT(ItemType type, CompoundTag tag) {
        return INSTANCE.writeNBT(type, tag);
    }

    public static short serializeMetadata(BlockState block) {
        return INSTANCE.readMetadata(block);
    }

    public static short serializeMetadata(ItemStack itemStack) {
        return INSTANCE.readMetadata(itemStack);
    }

    public static CompoundTag serializeNBT(BlockState block) {
        return INSTANCE.readNBT(block);
    }

    @Override
    public CompoundTag readNBT(BlockState block) {
        Serializer dataSerializer = SERIALIZERS.get(block.getBlockType().getId());
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
}
