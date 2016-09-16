package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeTakeItem implements NetworkPackage {
    private long itemEntityId;
    private long playerEntityId;

    @Override
    public void decode(ByteBuf buffer) {
        itemEntityId = buffer.readLong();
        playerEntityId = buffer.readLong();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(itemEntityId);
        buffer.writeLong(playerEntityId);
    }
}
