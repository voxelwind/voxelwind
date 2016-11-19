package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeUpdateBlock implements NetworkPackage {
    private Vector3i position;
    private int blockId;
    private int metadata;

    @Override
    public void decode(ByteBuf buffer) {
        position = McpeUtil.readBlockCoords(buffer);
        blockId = (int) Varints.decodeUnsigned(buffer);
        metadata = (int) Varints.decodeUnsigned(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeBlockCoords(buffer, position);
        Varints.encodeUnsigned(buffer, blockId);
        Varints.encodeUnsigned(buffer, metadata);
    }
}
