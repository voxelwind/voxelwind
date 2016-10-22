package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeResourcePackClientResponse implements NetworkPackage {
    private byte unknownByte;
    private short unknownShort;

    @Override
    public void decode(ByteBuf buffer) {
        unknownByte = buffer.readByte();
        unknownShort = buffer.readShort();
    }

    @Override
    public void encode(ByteBuf buffer) {

    }
}
