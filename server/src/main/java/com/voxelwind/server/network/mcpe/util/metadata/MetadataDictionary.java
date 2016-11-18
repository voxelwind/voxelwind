package com.voxelwind.server.network.mcpe.util.metadata;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.mcpe.McpeUtil;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.buffer.ByteBuf;

import java.nio.ByteOrder;
import java.util.Optional;

public final class MetadataDictionary {
    private final TIntObjectMap<Object> typeMap = new TIntObjectHashMap<>();

    public void putAll(MetadataDictionary dictionary) {
        Preconditions.checkNotNull(dictionary, "dictionary");
        typeMap.putAll(dictionary.typeMap);
    }

    public Optional get(int index) {
        return Optional.ofNullable(typeMap.get(index));
    }

    public void put(int index, Object o) {
        Preconditions.checkNotNull(o, "o");
        Preconditions.checkArgument(isAcceptable(o), "object can not be serialized");

        typeMap.put(index, o);
    }

    public void writeTo(ByteBuf buf) {
        Varints.encodeUnsigned(buf, typeMap.size());
        typeMap.forEachEntry((i, o) -> {
            serialize(buf, i, o);
            return true;
        });
    }

    private static boolean isAcceptable(Object o) {
        return o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Float || o
                instanceof String || o instanceof Vector3i || o instanceof Long;
    }

    public static MetadataDictionary deserialize(ByteBuf buf) {
        MetadataDictionary dictionary = new MetadataDictionary();
        int sz = Varints.decodeUnsigned(buf);
        for (int i = 0; i < sz; i++) {
            int idx = Varints.decodeUnsigned(buf);
            int type = Varints.decodeUnsigned(buf);

            switch (type) {
                case EntityMetadataConstants.DATA_TYPE_BYTE:
                    dictionary.put(idx, buf.readByte());
                    break;
                case EntityMetadataConstants.DATA_TYPE_SHORT:
                    dictionary.put(idx, buf.readShortLE());
                    break;
                case EntityMetadataConstants.DATA_TYPE_INT:
                    dictionary.put(idx, Varints.decodeSigned(buf));
                    break;
                case EntityMetadataConstants.DATA_TYPE_FLOAT:
                    dictionary.put(idx, McpeUtil.readFloatLE(buf));
                    break;
                case EntityMetadataConstants.DATA_TYPE_STRING:
                    dictionary.put(idx, McpeUtil.readVarintLengthString(buf));
                    break;
                case EntityMetadataConstants.DATA_TYPE_POS:
                    dictionary.put(idx, McpeUtil.readBlockCoords(buf));
                    break;
                case EntityMetadataConstants.DATA_TYPE_SLOT:
                    dictionary.put(idx, McpeUtil.readItemStack(buf));
                    break;
                case EntityMetadataConstants.DATA_TYPE_LONG:
                    dictionary.put(idx, Varints.decodeSignedLong(buf));
                    break;
                default:
                    throw new IllegalArgumentException("Type " + type + " is not recognized.");
            }
        }
        return dictionary;
    }

    private static void serialize(ByteBuf buf, int idx, Object o) {
        Preconditions.checkNotNull(buf, "buf");
        Preconditions.checkNotNull(o, "o");

        if (o instanceof Byte) {
            Byte aByte = (Byte) o;
            Varints.encodeUnsigned(buf, idx);
            Varints.encodeUnsigned(buf, EntityMetadataConstants.DATA_TYPE_BYTE);
            buf.writeByte(aByte);
        } else if (o instanceof Short) {
            Short aShort = (Short) o;
            Varints.encodeUnsigned(buf, idx);
            Varints.encodeUnsigned(buf, EntityMetadataConstants.DATA_TYPE_SHORT);
            buf.writeShortLE(aShort);
        } else if (o instanceof Integer) {
            Integer integer = (Integer) o;
            Varints.encodeUnsigned(buf, idx);
            Varints.encodeUnsigned(buf, EntityMetadataConstants.DATA_TYPE_INT);
            Varints.encodeSigned(buf, integer);
        } else if (o instanceof Float) {
            Float aFloat = (Float) o;
            Varints.encodeUnsigned(buf, idx);
            Varints.encodeUnsigned(buf, EntityMetadataConstants.DATA_TYPE_FLOAT);
            McpeUtil.writeFloatLE(buf, aFloat);
        } else if (o instanceof String) {
            String s = (String) o;
            Varints.encodeUnsigned(buf, idx);
            Varints.encodeUnsigned(buf, EntityMetadataConstants.DATA_TYPE_STRING);
            McpeUtil.writeVarintLengthString(buf, s);
        } else if (o instanceof ItemStack) {
            ItemStack stack = (ItemStack) o;
            Varints.encodeUnsigned(buf, idx);
            Varints.encodeUnsigned(buf, EntityMetadataConstants.DATA_TYPE_SLOT);
            McpeUtil.writeItemStack(buf, stack);
        } else if (o instanceof Vector3i) {
            Vector3i vector3i = (Vector3i) o;
            Varints.encodeUnsigned(buf, idx);
            Varints.encodeUnsigned(buf, EntityMetadataConstants.DATA_TYPE_POS);
            McpeUtil.writeBlockCoords(buf, vector3i);
        } else if (o instanceof Long) {
            Long l = (Long) o;
            Varints.encodeUnsigned(buf, idx);
            Varints.encodeUnsigned(buf, EntityMetadataConstants.DATA_TYPE_LONG);
            Varints.encodeSignedLong(buf, l);
        } else {
            throw new IllegalArgumentException("Unsupported type " + o.getClass().getName());
        }
    }

    @Override
    public String toString() {
        return typeMap.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetadataDictionary that = (MetadataDictionary) o;
        return java.util.Objects.equals(typeMap, that.typeMap);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(typeMap);
    }
}
