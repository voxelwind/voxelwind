package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeSetSpawnPosition implements NetworkPackage {
    private Vector3i position;

    @Override
    public void decode(ByteBuf buffer) {
        position = McpeUtil.readVector3i(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeVector3i(buffer, position);
    }
}
