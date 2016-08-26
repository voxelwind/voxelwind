package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeEntityEvent implements RakNetPackage {
    private long entityId;
    private byte event;

    @Override
    public void decode(ByteBuf buffer) {
        entityId = buffer.readLong();
        event = buffer.readByte();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(entityId);
        buffer.writeByte(event);
    }
}
