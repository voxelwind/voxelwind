package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeResourcePackClientResponse implements NetworkPackage {
    private byte responseStatus;
    private short resourcePackIdVersions;


    @Override
    public void decode(ByteBuf buffer) {
        responseStatus = buffer.readByte();
        resourcePackIdVersions = buffer.readShort();
    }

    @Override
    public void encode(ByteBuf buffer) {

    }
}
