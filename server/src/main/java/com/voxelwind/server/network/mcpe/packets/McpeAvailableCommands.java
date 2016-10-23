package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeAvailableCommands implements NetworkPackage {
    private String commandJson;
    private String unknown = "";

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeVarintLengthString(buffer, commandJson);
        McpeUtil.writeVarintLengthString(buffer, unknown);
    }
}
