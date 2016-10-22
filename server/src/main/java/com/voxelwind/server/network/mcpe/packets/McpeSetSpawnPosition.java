package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeSetSpawnPosition implements NetworkPackage {
    private int unknown1;
    private Vector3i position;
    private boolean unknown2;

    @Override
    public void decode(ByteBuf buffer) {
        unknown1 = Varints.decodeSigned(buffer);
        position = McpeUtil.readBlockCoords(buffer);
        buffer.writeBoolean(unknown2);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSigned(unknown1, buffer);
        McpeUtil.writeBlockCoords(buffer, position);
        buffer.writeBoolean(unknown2);
    }
}
