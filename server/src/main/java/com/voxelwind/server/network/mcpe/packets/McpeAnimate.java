package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeAnimate implements RakNetPackage {
    private byte action;
    private long entityId;

    @Override
    public void decode(ByteBuf buffer) {
        action = buffer.readByte();
        entityId = buffer.readLong();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(action);
        buffer.writeLong(entityId);
    }
}
