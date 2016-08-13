package com.voxelwind.server.level.entities.metadata;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

public class MetadataDictionary {
    private final Map<Integer, Object> typeMap = new HashMap<>();

    public void writeTo(ByteBuf buf) {
        for (Map.Entry<Integer, Object> entry : typeMap.entrySet()) {
            serialize(buf, entry.getKey(), entry.getValue());
        }
        buf.writeByte(0x7F);
    }

    private static void serialize(ByteBuf buf, int idx, Object o) {
        Preconditions.checkNotNull(buf, "buf");
        Preconditions.checkNotNull(o, "o");

        if (o instanceof Byte) {
            Byte aByte = (Byte) o;
            buf.writeByte(EntityMetadataConstants.idify(EntityMetadataConstants.DATA_TYPE_BYTE, idx));
            buf.writeByte(aByte);
        } else if (o instanceof Short) {
            Short aShort = (Short) o;
            buf.writeByte(EntityMetadataConstants.idify(EntityMetadataConstants.DATA_TYPE_SHORT, idx));
            buf.writeShort(aShort);
        } else if (o instanceof Integer) {
            Integer integer = (Integer) o;
            buf.writeByte(EntityMetadataConstants.idify(EntityMetadataConstants.DATA_TYPE_INT, idx));
            buf.writeInt(integer);
        } else if (o instanceof Float) {
            Float aFloat = (Float) o;
            buf.writeByte(EntityMetadataConstants.idify(EntityMetadataConstants.DATA_TYPE_FLOAT, idx));
            buf.writeFloat(aFloat);
        } else if (o instanceof String) {
            String s = (String) o;
            buf.writeByte(EntityMetadataConstants.idify(EntityMetadataConstants.DATA_TYPE_STRING, idx));
            RakNetUtil.writeString(buf, s);
        } // TODO: Implement slots.
        else if (o instanceof Vector3i) {
            Vector3i vector3i = (Vector3i) o;
            buf.writeByte(EntityMetadataConstants.idify(EntityMetadataConstants.DATA_TYPE_POS, idx));
            McpeUtil.writeVector3i(buf, vector3i, false);
        }

        throw new IllegalArgumentException("Unsupported type " + o);
    }
}
