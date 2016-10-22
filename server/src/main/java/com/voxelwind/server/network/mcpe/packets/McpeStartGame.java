package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeStartGame implements NetworkPackage {
    private long entityId; // = null;
    private long runtimeEntityId; // = null;
    private Vector3f spawn; // = null;
    private Vector2i unknown1; // = null;
    private int seed; // = null;
    private int dimension; // = null;
    private int generator; // = null;
    private int gamemode; // = null;
    private int difficulty; // = null;
    private int x; // = null;
    private int y; // = null;
    private int z; // = null;
    private boolean hasAchievementsDisabled; // = null;
    private int dayCycleStopTime; // = null;
    private boolean eduMode; // = null;
    private float rainLevel; // = null;
    private float lightingLevel; // = null;
    private boolean enableCommands; // = null;
    private boolean isTexturepacksRequired; // = null;
    private String secret; // = null;
    private String worldName; // = null;

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSigned(entityId, buffer);
        Varints.encodeSigned(runtimeEntityId, buffer);
        McpeUtil.writeVector3f(buffer, spawn);
        // TODO: what are these next two?
        buffer.writeFloat(0);
        buffer.writeFloat(0);
        Varints.encodeSigned(seed, buffer);
        Varints.encodeSigned(dimension, buffer);
        Varints.encodeSigned(generator, buffer);
        Varints.encodeSigned(gamemode, buffer);
        Varints.encodeSigned(dimension, buffer);
        McpeUtil.writeBlockCoords(buffer, spawn.toInt());
        buffer.writeBoolean(hasAchievementsDisabled);
        Varints.encodeSigned(dayCycleStopTime, buffer);
        buffer.writeBoolean(eduMode);
        buffer.writeFloat(rainLevel);
        buffer.writeFloat(lightingLevel);
        buffer.writeBoolean(enableCommands);
        buffer.writeBoolean(isTexturepacksRequired);
        McpeUtil.writeVarintLengthString(buffer, secret);
        McpeUtil.writeVarintLengthString(buffer, worldName);
    }
}
