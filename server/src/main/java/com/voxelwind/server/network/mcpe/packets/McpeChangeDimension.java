package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeChangeDimension implements NetworkPackage {
    private int dimension;
    private Vector3f position;
    private boolean unknown;

    @Override
    public void decode(ByteBuf buffer) {
        dimension = Varints.decodeSigned(buffer);
        position = McpeUtil.readVector3f(buffer);
        unknown = buffer.readBoolean();
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSigned(buffer, dimension);
        McpeUtil.writeVector3f(buffer, position);
        buffer.writeBoolean(unknown);
    }
}
