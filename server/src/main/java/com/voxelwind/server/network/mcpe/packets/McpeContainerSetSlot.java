package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeContainerSetSlot implements RakNetPackage {
    private byte windowId;
    private short slot;
    private short unknown;
    private ItemStack stack;

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        slot = buffer.readShort();
        unknown = buffer.readShort(); // Unknown
        stack = McpeUtil.readItemStack(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        buffer.writeShort(slot);
        buffer.writeShort(unknown);
        McpeUtil.writeItemStack(buffer, stack);
    }
}
