package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeAdventureSettings implements NetworkPackage {
    private int flags;
    private int playerPermissions;

    @Override
    public void decode(ByteBuf buffer) {
        flags = (int) Varints.decodeUnsigned(buffer);
        playerPermissions = (int) Varints.decodeUnsigned(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeUnsigned(buffer, flags);
        Varints.encodeUnsigned(buffer, playerPermissions);
    }
}
