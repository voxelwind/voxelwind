package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeContainerSetData implements NetworkPackage {
    public byte windowId;
    public short property;
    public short value;

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        property = buffer.readShort();
        value = buffer.readShort();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        buffer.writeShort(property);
        buffer.writeShort(value);
    }
}
