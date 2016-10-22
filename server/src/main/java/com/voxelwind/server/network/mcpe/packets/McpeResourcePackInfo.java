package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.util.ResourcePackInfo;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class McpeResourcePackInfo implements NetworkPackage {
    private boolean mustAccept;
    private final List<ResourcePackInfo> behaviorPacks = new ArrayList<>();
    private final List<ResourcePackInfo> resourcePacks = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBoolean(mustAccept);
        buffer.writeShort(behaviorPacks.size());
        for (ResourcePackInfo behaviorPack : behaviorPacks) {
            McpeUtil.writeResourcePackInfo(buffer, behaviorPack);
        }
        buffer.writeShort(resourcePacks.size());
        for (ResourcePackInfo resourcePack : resourcePacks) {
            McpeUtil.writeResourcePackInfo(buffer, resourcePack);
        }
    }
}
