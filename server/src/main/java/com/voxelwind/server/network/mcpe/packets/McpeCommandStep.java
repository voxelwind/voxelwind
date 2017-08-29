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
    private int currentStep;
    private boolean unknown3;
    private long clientId;
    private String inputJson;
    private String outputJson;

    @Override
    public void decode(ByteBuf buffer) {
        command = McpeUtil.readVarintLengthString(buffer);
        overload = McpeUtil.readVarintLengthString(buffer);
        unknown1 = (int) Varints.decodeUnsigned(buffer);
        currentStep = (int) Varints.decodeUnsigned(buffer);
        unknown3 = buffer.readBoolean();
        clientId = (int) Varints.decodeUnsigned(buffer);
        inputJson = McpeUtil.readVarintLengthString(buffer);
        outputJson = McpeUtil.readVarintLengthString(buffer);
        buffer.skipBytes(buffer.readableBytes());
    }

    @Override
    public void encode(ByteBuf buffer) {

    }
}
