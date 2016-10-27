package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeAnimate implements NetworkPackage {
    private int action;
    private long entityId;

    @Override
    public void decode(ByteBuf buffer) {
        action = Varints.decodeSigned(buffer);
        entityId = Varints.decodeSignedLong(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSigned(buffer, action);
        Varints.encodeSignedLong(buffer, entityId);
    }
}
