package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeInteract implements NetworkPackage {
    private byte type;
    private long entityId;

    @Override
    public void decode(ByteBuf buffer) {
        type = buffer.readByte();
        entityId = Varints.decodeUnsigned(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(type);
        Varints.encodeUnsigned(buffer, entityId);
    }
}
