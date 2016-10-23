package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeContainerSetData implements NetworkPackage {
    public byte windowId;
    public int property;
    public int value;

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        property = Varints.decodeSigned(buffer);
        value = Varints.decodeSigned(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        Varints.encodeSigned(buffer, property);
        Varints.encodeSigned(buffer, value);
    }
}
