package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeTakeItem implements NetworkPackage {
    private long itemEntityId;
    private long playerEntityId;

    @Override
    public void decode(ByteBuf buffer) {
        itemEntityId = Varints.decodeSignedLong(buffer);
        playerEntityId = Varints.decodeSignedLong(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSignedLong(buffer, itemEntityId);
        Varints.encodeSignedLong(buffer, playerEntityId);
    }
}
