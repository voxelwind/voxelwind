package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeContainerClose implements RakNetPackage {
    private byte windowId;

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
    }
}
