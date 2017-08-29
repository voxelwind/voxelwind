package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.util.metadata.MetadataDictionary;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeSetEntityData implements NetworkPackage {
    private long entityId;
    private final MetadataDictionary metadata = new MetadataDictionary();

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeUnsigned(buffer, entityId);
        metadata.writeTo(buffer);
    }
}
