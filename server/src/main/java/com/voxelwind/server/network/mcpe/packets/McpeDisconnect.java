package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeDisconnect implements NetworkPackage {
    private boolean hideScreen;
    private String message;

    @Override
    public void decode(ByteBuf buffer) {
        hideScreen = buffer.readBoolean();
        message = McpeUtil.readVarintLengthString(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBoolean(hideScreen);
        McpeUtil.writeVarintLengthString(buffer, message);
    }
}
