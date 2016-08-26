package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.mcpe.util.metadata.MetadataDictionary;
import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeSetEntityData implements RakNetPackage {
    private long entityId;
    private final MetadataDictionary metadata = new MetadataDictionary();

    @Override
    public void decode(ByteBuf buffer) {
        entityId = buffer.readLong();
        metadata.putAll(MetadataDictionary.deserialize(buffer));
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(entityId);
        metadata.writeTo(buffer);
    }
}
