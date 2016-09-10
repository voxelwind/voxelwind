package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeUseItem implements NetworkPackage {
    private Vector3i location;
    private byte face;
    private Vector3f facePosition;
    private Vector3f position;
    private int unknown;
    private ItemStack stack;

    @Override
    public void decode(ByteBuf buffer) {
        location = McpeUtil.readVector3i(buffer);
        face = buffer.readByte();
        facePosition = McpeUtil.readVector3f(buffer);
        position = McpeUtil.readVector3f(buffer);
        unknown = buffer.readInt();
        stack = McpeUtil.readItemStack(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }
}
