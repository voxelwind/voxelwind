package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeCommandStep implements NetworkPackage {
    private String command;
    private String overload;
    private int unknown1;
    private int unknown2;
    private boolean unknown3;
    private long unknown4;
    private String args;
    private String unknown5;

    @Override
    public void decode(ByteBuf buffer) {
        command = McpeUtil.readVarintLengthString(buffer);
        overload = McpeUtil.readVarintLengthString(buffer);
        unknown1 = Varints.decodeUnsigned(buffer);
        unknown2 = Varints.decodeUnsigned(buffer);
        unknown3 = buffer.readBoolean();
        unknown4 = Varints.decodeUnsignedLong(buffer);
        args = McpeUtil.readVarintLengthString(buffer);
        unknown5 = McpeUtil.readVarintLengthString(buffer);
        buffer.skipBytes(buffer.readableBytes());
    }

    @Override
    public void encode(ByteBuf buffer) {

    }
}
