package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeResourcePackClientResponse implements NetworkPackage {
    private byte responceStatus;
    private short resourcePackIdVersions;


    @Override
    public void decode(ByteBuf buffer) {
        responceStatus = buffer.readByte();
        resourcePackIdVersions = buffer.readShort();
    }

    @Override
    public void encode(ByteBuf buffer) {

    }
}
