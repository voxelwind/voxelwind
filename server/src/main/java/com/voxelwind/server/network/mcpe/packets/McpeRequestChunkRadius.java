package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeRequestChunkRadius implements NetworkPackage {
    private int radius;

    @Override
    public void decode(ByteBuf buffer) {
        radius = buffer.readInt();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(radius);
    }
}
