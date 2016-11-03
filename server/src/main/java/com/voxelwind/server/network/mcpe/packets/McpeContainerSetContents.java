package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeContainerSetContents implements NetworkPackage {
    private byte windowId;
    private ItemStack[] stacks;
    private int[] hotbarData = new int[0];

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        int stacksToRead = Varints.decodeUnsigned(buffer);
        stacks = new ItemStack[stacksToRead];
        for (int i = 0; i < stacksToRead; i++) {
            stacks[i] = McpeUtil.readItemStack(buffer);
        }

        if (windowId == 0) {
            int hotbarEntriesToRead = Varints.decodeUnsigned(buffer);
            hotbarData = new int[hotbarEntriesToRead];
            for (int i = 0; i < hotbarEntriesToRead; i++) {
                hotbarData[i] = Varints.decodeSigned(buffer);
            }
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        Varints.encodeUnsigned(buffer, stacks.length);
        for (ItemStack stack : stacks) {
            McpeUtil.writeItemStack(buffer, stack);
        }
        if (windowId == 0) {
            Varints.encodeUnsigned(buffer, hotbarData.length);
            for (int i : hotbarData) {
                Varints.encodeSigned(buffer, i);
            }
        }
    }
}
