package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeContainerSetSlot implements NetworkPackage {
    private byte windowId;
    private int slot;
    private int unknown;
    private ItemStack stack;

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        slot = Varints.decodeSigned(buffer);
        unknown = Varints.decodeSigned(buffer);
        stack = McpeUtil.readItemStack(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        Varints.encodeSigned(buffer, slot);
        Varints.encodeSigned(buffer, unknown);
        McpeUtil.writeItemStack(buffer, stack);
    }
}
