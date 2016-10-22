package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpePlayerAction implements NetworkPackage {
    private long entityId;
    private Action action;
    private Vector3i position;
    private int face;

    @Override
    public void decode(ByteBuf buffer) {
        entityId = Varints.decodeSignedLong(buffer);
        action = Action.values()[Varints.decodeSigned(buffer)];
        position = McpeUtil.readBlockCoords(buffer);
        face = Varints.decodeSigned(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSignedLong(buffer, entityId);
        Varints.encodeSigned(buffer, action.ordinal());
        McpeUtil.writeBlockCoords(buffer, position);
        Varints.encodeSigned(buffer, face);
    }

    public enum Action {
        ACTION_START_BREAK,
        ACTION_ABORT_BREAK,
        ACTION_STOP_BREAK,
        ACTION_RELEASE_ITEM,
        ACTION_STOP_SLEEPING,
        ACTION_SPAWN_SAME_DIMENSION,
        ACTION_JUMP,
        ACTION_START_SPRINT,
        ACTION_STOP_SPRINT,
        ACTION_START_SNEAK,
        ACTION_STOP_SNEAK,
        ACTION_SPAWN_OVERWORLD,
        ACTION_SPAWN_NETHER
    }
}
