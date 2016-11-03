package com.voxelwind.server.network.raknet.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.annotations.BatchDisallowed;
import com.voxelwind.server.network.mcpe.annotations.DisallowWrapping;
import com.voxelwind.server.network.mcpe.annotations.ForceClearText;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@BatchDisallowed
@ForceClearText
@DisallowWrapping
@Data
public class ConnectedPongPacket implements NetworkPackage {
    private long pingTime;
    private long pongTime;

    @Override
    public void decode(ByteBuf buffer) {
        pingTime = buffer.readLong();
        pongTime = buffer.readLong();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(pingTime);
        buffer.writeLong(pongTime);
    }
}
