package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeRequestChunkRadius implements NetworkPackage {
    private int radius;

    @Override
    public void decode(ByteBuf buffer) {
        radius = Varints.decodeSigned(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSigned(radius, buffer);
    }
}
