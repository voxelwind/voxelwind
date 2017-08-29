package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.game.level.util.Attribute;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class McpeUpdateAttributes implements NetworkPackage {
    private long entityId;
    private final Collection<Attribute> attributes = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        entityId = Varints.decodeUnsigned(buffer);
        attributes.addAll(McpeUtil.readAttributes(buffer));
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeUnsigned(buffer, entityId);
        McpeUtil.writeAttributes(buffer, attributes);
    }
}
