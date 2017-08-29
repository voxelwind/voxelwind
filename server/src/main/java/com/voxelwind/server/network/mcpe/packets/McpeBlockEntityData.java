package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.nbt.io.NBTEncoding;
import com.voxelwind.nbt.io.NBTWriter;
import com.voxelwind.nbt.tags.Tag;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.util.LittleEndianByteBufOutputStream;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.io.IOException;

@Data
public class McpeBlockEntityData implements NetworkPackage {
    private Vector3i position;
    private Tag<?> blockEntityData;

    @Override
    public void decode(ByteBuf buffer) {
        position = McpeUtil.readBlockCoords(buffer);
        // TODO: BlockEntityData
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeBlockCoords(buffer, position);
        try (NBTWriter writer = new NBTWriter(new LittleEndianByteBufOutputStream(buffer), NBTEncoding.MCPE_0_16_NETWORK)) {
            writer.write(blockEntityData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
