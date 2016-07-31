package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

public class McpeSetTime implements RakNetPackage {
    private long time;
    private boolean running;

    @Override
    public void decode(ByteBuf buffer) {
        time = buffer.readLong();
        running = buffer.readBoolean();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(time);
        buffer.writeBoolean(running);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
