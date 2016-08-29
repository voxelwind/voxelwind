package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpePlayStatus implements NetworkPackage {
    private Status status;

    @Override
    public void decode(ByteBuf buffer) {
        status = Status.values()[buffer.readInt()];
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(status.ordinal());
    }

    public enum Status {
        LOGIN_SUCCESS,
        LOGIN_FAILED_CLIENT,
        LOGIN_FAILED_SERVER,
        PLAYER_SPAWN
    }
}
