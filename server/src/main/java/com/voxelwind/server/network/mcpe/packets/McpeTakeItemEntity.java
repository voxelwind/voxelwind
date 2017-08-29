package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeTakeItemEntity implements NetworkPackage {
    private long itemEntityId;
    private long playerEntityId;

    @Override
    public void decode(ByteBuf buffer) {
        itemEntityId = Varints.decodeUnsigned(buffer);
        playerEntityId = Varints.decodeUnsigned(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeUnsigned(buffer, itemEntityId);
        Varints.encodeUnsigned(buffer, playerEntityId);
    }
}
