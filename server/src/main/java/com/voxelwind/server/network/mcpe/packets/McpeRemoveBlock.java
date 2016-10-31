package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeRemoveBlock implements NetworkPackage {
    private Vector3i position;

    @Override
    public void decode(ByteBuf buffer) {
        position = McpeUtil.readBlockCoords(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeBlockCoords(buffer, position);
    }
}
