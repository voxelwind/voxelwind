package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeUpdateBlock implements NetworkPackage {
    private Vector3i position;
    private byte blockId;
    private byte metadata;

    @Override
    public void decode(ByteBuf buffer) {
        position = McpeUtil.readVector3i(buffer);
        blockId = buffer.readByte();
        metadata = buffer.readByte();
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeVector3i(buffer, position);
        buffer.writeByte(blockId);
        buffer.writeByte(metadata);
    }
}
