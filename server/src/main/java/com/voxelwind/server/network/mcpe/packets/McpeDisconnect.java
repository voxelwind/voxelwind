package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeDisconnect implements NetworkPackage {
    private boolean hideDisconnectionScreen;
    private String message;

    @Override
    public void decode(ByteBuf buffer) {
        hideDisconnectionScreen = buffer.readBoolean();
        message = McpeUtil.readVarintLengthString(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBoolean(hideDisconnectionScreen);
        if (!hideDisconnectionScreen) {
            McpeUtil.writeVarintLengthString(buffer, message);
        }
    }
}
