package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeDropItem implements NetworkPackage {
    private byte unknown;
    private ItemStack unknown2;

    @Override
    public void decode(ByteBuf buffer) {
        unknown = buffer.readByte();
        unknown2 = McpeUtil.readItemStack(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }
}
