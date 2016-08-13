package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

public class McpeFullChunkData implements RakNetPackage {
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
