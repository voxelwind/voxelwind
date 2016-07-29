package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

public class McpeFullChunkData implements RakNetPackage {
    private int chunkX;
    private int chunkZ;
    private byte order;
    private ByteBuf data;

    @Override
    public void decode(ByteBuf buffer) {
        chunkX = buffer.readInt();
        chunkZ = buffer.readInt();
        order = buffer.readByte();
        short length = buffer.readShort();
        data = buffer.readSlice(length);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(chunkX);
        buffer.writeInt(chunkZ);
        buffer.writeByte(order);
        buffer.writeShort(data.readableBytes());
        buffer.writeBytes(data);
    }

    public int getChunkX() {
        return chunkX;
    }

    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public void setChunkZ(int chunkZ) {
        this.chunkZ = chunkZ;
    }

    public byte getOrder() {
        return order;
    }

    public void setOrder(byte order) {
        this.order = order;
    }

    public ByteBuf getData() {
        return data;
    }

    public void setData(ByteBuf data) {
        this.data = data;
    }
}
