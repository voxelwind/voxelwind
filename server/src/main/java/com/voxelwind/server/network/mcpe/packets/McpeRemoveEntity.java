package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeRemoveEntity implements NetworkPackage {
    private long entityId;

    @Override
    public void decode(ByteBuf buffer) {
        entityId = buffer.readLong();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(entityId);
    }
}
