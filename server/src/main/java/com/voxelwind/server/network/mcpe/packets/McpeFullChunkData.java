package com.voxelwind.server.network.mcpe.packets;

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
        chunkX = buffer.readInt();
        chunkZ = buffer.readInt();
        order = buffer.readByte();
        int length = buffer.readInt();
        data = new byte[length];
        buffer.readBytes(data);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(chunkX);
        buffer.writeInt(chunkZ);
        buffer.writeByte(order);
        buffer.writeInt(data.length);
        buffer.writeBytes(data);
    }
}
