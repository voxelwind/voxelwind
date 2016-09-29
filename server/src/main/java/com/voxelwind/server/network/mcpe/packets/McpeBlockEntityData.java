package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import lombok.Data;

import java.io.IOException;
import java.nio.ByteOrder;

@Data
public class McpeBlockEntityData implements NetworkPackage {
    private Vector3i position;
    private Tag<?> blockEntityData;

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeVector3i(buffer, position, false);
        try (NBTOutputStream stream = new NBTOutputStream(new ByteBufOutputStream(buffer), false, ByteOrder.LITTLE_ENDIAN)) {
            stream.writeTag(blockEntityData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
