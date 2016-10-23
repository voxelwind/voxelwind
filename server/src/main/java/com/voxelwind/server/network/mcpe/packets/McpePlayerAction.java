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
    private int action;
    private Vector3i position;
    private int face;

    @Override
    public void decode(ByteBuf buffer) {
        entityId = Varints.decodeSignedLong(buffer);
        action = Varints.decodeSigned(buffer);
        position = McpeUtil.readBlockCoords(buffer);
        face = Varints.decodeSigned(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSignedLong(buffer, entityId);
        Varints.encodeSigned(buffer, action);
        McpeUtil.writeBlockCoords(buffer, position);
        Varints.encodeSigned(buffer, face);
    }

	public static final int ACTION_START_BREAK = 0;
	public static final int ACTION_ABORT_BREAK = 1;
	public static final int ACTION_STOP_BREAK = 2;
	public static final int ACTION_RELEASE_ITEM = 5;
	public static final int ACTION_STOP_SLEEPING = 6;
	public static final int ACTION_RESPAWN = 7;
	public static final int ACTION_JUMP = 8;
	public static final int ACTION_START_SPRINT = 9;
	public static final int ACTION_STOP_SPRINT = 10;
	public static final int ACTION_START_SNEAK = 11;
	public static final int ACTION_STOP_SNEAK = 12;
	public static final int ACTION_DIMENSION_CHANGE = 13; //TODO: correct these
}
