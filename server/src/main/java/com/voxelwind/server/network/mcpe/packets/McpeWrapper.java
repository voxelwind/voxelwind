package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.annotations.DisallowWrapping;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@DisallowWrapping // this is the wrapper!
@Data
public class McpeWrapper implements NetworkPackage {
    private ByteBuf payload;
    private final List<NetworkPackage> packets = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        payload = buffer.readSlice(buffer.readableBytes());
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBytes(payload);
    }
}
