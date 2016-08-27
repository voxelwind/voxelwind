package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class McpeContainerSetContents implements RakNetPackage {
    private byte windowId;
    private final Map<Integer, ItemStack> stacks = new HashMap<>();
    private int[] hotbarData = new int[0];

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        int stacksToRead = buffer.readShort();
        for (int i = 0; i < stacksToRead; i++) {
            stacks.put(i, McpeUtil.readItemStack(buffer));
        }
        int hotbarEntriesToRead = buffer.readShort();
        hotbarData = new int[hotbarEntriesToRead];
        for (int i = 0; i < hotbarEntriesToRead; i++) {
            hotbarData[i] = buffer.readInt();
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        buffer.writeShort(stacks.size());
        for (ItemStack stack : stacks.values()) {
            McpeUtil.writeItemStack(buffer, stack);
        }
        buffer.writeShort(hotbarData.length);
        for (int i : hotbarData) {
            buffer.writeInt(i);
        }
    }
}
