package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.util.Rotation;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.util.metadata.MetadataDictionary;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.UUID;

@Data
public class McpeAddPlayer implements NetworkPackage {
    private UUID uuid;
    private String username;
    private long entityId;
    private long runtimeEntityId;
    private Vector3f position;
    private Vector3f velocity;
    private Rotation rotation;
    private ItemStack held = new VoxelwindItemStack(BlockTypes.AIR, 1, null);
    private final MetadataDictionary metadata = new MetadataDictionary();

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeUuid(buffer, uuid);
        McpeUtil.writeVarintLengthString(buffer, username);
        Varints.encodeSignedLong(buffer, entityId);
        Varints.encodeUnsigned(buffer, runtimeEntityId);
        McpeUtil.writeVector3f(buffer, position);
        McpeUtil.writeVector3f(buffer, velocity);
        McpeUtil.writeRotation(buffer, rotation);
        McpeUtil.writeItemStack(buffer, held);
        metadata.writeTo(buffer);
    }
}
