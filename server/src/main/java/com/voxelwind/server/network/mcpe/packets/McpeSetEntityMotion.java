package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class McpeSetEntityMotion implements NetworkPackage {
    private long entityId;
    private Vector3f motion;

    @Override
    public void decode(ByteBuf buffer) {
        entityId = Varints.decodeSignedLong(buffer);
        motion = McpeUtil.readVector3f(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSignedLong(buffer, entityId);
        McpeUtil.writeVector3f(buffer, motion);
    }
}
