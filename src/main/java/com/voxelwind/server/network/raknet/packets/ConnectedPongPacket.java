package com.voxelwind.server.network.raknet.packets;

import com.voxelwind.server.network.mcpe.annotations.BatchDisallowed;
import com.voxelwind.server.network.mcpe.annotations.ForceClearText;
import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

@BatchDisallowed
@ForceClearText
public class ConnectedPongPacket implements RakNetPackage {
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

    public long getPingTime() {
        return pingTime;
    }

    public void setPingTime(long pingTime) {
        this.pingTime = pingTime;
    }

    public long getPongTime() {
        return pongTime;
    }

    public void setPongTime(long pongTime) {
        this.pongTime = pongTime;
    }
}
