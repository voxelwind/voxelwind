package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeContainerOpen implements NetworkPackage {
    private byte windowId;
    private byte type;
    private int slotCount;
    private Vector3i position;
    private long runtimeEntityId;

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        type = buffer.readByte();
        slotCount = buffer.readShort();
        position = McpeUtil.readBlockCoords(buffer);
        runtimeEntityId = Varints.decodeUnsigned(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        buffer.writeByte(type);
        buffer.writeShort(slotCount);
        McpeUtil.writeBlockCoords(buffer, position);
        Varints.encodeUnsigned(buffer, runtimeEntityId);
    }
}
