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
    private int hotbarSlot;
    private ItemStack stack;
    private byte selectSlot;

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        slot = Varints.decodeSigned(buffer);
        hotbarSlot = Varints.decodeSigned(buffer);
        stack = McpeUtil.readItemStack(buffer);
        selectSlot = buffer.readByte();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        Varints.encodeSigned(buffer, slot);
        Varints.encodeSigned(buffer, hotbarSlot);
        McpeUtil.writeItemStack(buffer, stack);
        buffer.writeByte(selectSlot);
    }
}
