package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeSetTime implements NetworkPackage {
    private int time;
    private boolean running;

    @Override
    public void decode(ByteBuf buffer) {
        time = buffer.readInt();
        running = buffer.readBoolean();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(time);
        buffer.writeBoolean(running);
    }
}
