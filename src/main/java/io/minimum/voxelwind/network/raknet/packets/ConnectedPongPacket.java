package io.minimum.voxelwind.network.raknet.packets;

import io.minimum.voxelwind.network.mcpe.annotations.BatchDisallowed;
import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

@BatchDisallowed
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
