package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeAdventureSettings implements NetworkPackage {
    private int flags;
    private int playerPermissions;
    private int globalPermissions;

    @Override
    public void decode(ByteBuf buffer) {
        flags = buffer.readInt();
        playerPermissions = buffer.readInt();
        globalPermissions = buffer.readInt();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(flags);
        buffer.writeInt(playerPermissions);
        buffer.writeInt(globalPermissions);
    }
}
