package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeSetPlayerGameMode implements RakNetPackage {
    private int gamemode;

    @Override
    public void decode(ByteBuf buffer) {
        gamemode = buffer.readInt();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(gamemode);
    }
}
