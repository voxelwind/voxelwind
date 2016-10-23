package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeEntityEvent implements NetworkPackage {
    public static final byte HURT_ANIMATION = 2;
	public static final byte DEATH_ANIMATION = 3;
	public static final byte TAME_FAIL = 6;
	public static final byte TAME_SUCCESS = 7;
	public static final byte SHAKE_WET = 8;
	public static final byte USE_ITEM = 9;
	public static final byte EAT_GRASS_ANIMATION = 10;
	public static final byte FISH_HOOK_BUBBLE = 11;
	public static final byte FISH_HOOK_POSITION = 12;
	public static final byte FISH_HOOK_HOOK = 13;
	public static final byte FISH_HOOK_TEASE = 14;
	public static final byte SQUID_INK_CLOUD = 15;
	public static final byte AMBIENT_SOUND = 16;
	public static final byte RESPAWN = 17;

    private long entityId;
    private byte event;
    private int unknown;

    @Override
    public void decode(ByteBuf buffer) {
        entityId = Varints.decodeSignedLong(buffer);
        event = buffer.readByte();
        unknown = Varints.decodeSigned(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSignedLong(buffer, entityId);
        buffer.writeByte(event);
        Varints.encodeSigned(buffer, unknown);
    }
}
