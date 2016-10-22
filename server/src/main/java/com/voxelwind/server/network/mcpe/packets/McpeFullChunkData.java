package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(exclude = {"data"})
@EqualsAndHashCode(exclude = {"data"})
public class McpeFullChunkData implements NetworkPackage {
    private int chunkX;
    private int chunkZ;
    private byte order;
    private byte[] data;

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSigned(buffer, chunkX);
        Varints.encodeSigned(buffer, chunkZ);
        buffer.writeByte(order);
        Varints.encodeUnsigned(buffer, data.length);
        buffer.writeBytes(data);
    }
}
