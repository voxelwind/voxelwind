package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeSetSpawnPosition implements NetworkPackage {
    private SpawnType spawnType;
    private Vector3i position;
    private boolean spawnForced;

    @Override
    public void decode(ByteBuf buffer) {
        spawnType = SpawnType.values()[Varints.decodeSigned(buffer)];
        position = McpeUtil.readBlockCoords(buffer);
        buffer.writeBoolean(spawnForced);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSigned(buffer, spawnType.ordinal());
        McpeUtil.writeBlockCoords(buffer, position);
        buffer.writeBoolean(spawnForced);
    }

    public enum SpawnType {
        PLAYER_SPAWN,
        WORLD_SPAWN
    }
}
