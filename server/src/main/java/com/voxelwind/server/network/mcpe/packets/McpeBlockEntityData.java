package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.nbt.io.NBTWriter;
import com.voxelwind.nbt.tags.Tag;
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
        try (NBTWriter writer = new NBTWriter(new ByteBufOutputStream(buffer.order(ByteOrder.LITTLE_ENDIAN)))) {
            writer.write(blockEntityData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
