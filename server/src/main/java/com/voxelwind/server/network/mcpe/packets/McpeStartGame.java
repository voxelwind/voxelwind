package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
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
    private Vector3i worldSpawn; // = null;
    private boolean hasAchievementsDisabled; // = null;
    private int dayCycleStopTime; // = null;
    private boolean eduMode; // = null;
    private float rainLevel; // = null;
    private float lightingLevel; // = null;
    private boolean enableCommands; // = null;
    private boolean isTexturepacksRequired; // = null;
    private String levelId; // = null;
    private String worldName; // = null;

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSignedLong(buffer, entityId);
        Varints.encodeUnsignedLong(buffer, runtimeEntityId);
        McpeUtil.writeVector3f(buffer, spawn);
        // TODO: what are these next two?
        buffer.writeFloat(0);
        buffer.writeFloat(0);
        Varints.encodeSigned(buffer, seed);
        Varints.encodeSigned(buffer, dimension);
        Varints.encodeSigned(buffer, generator);
        Varints.encodeSigned(buffer, gamemode);
        Varints.encodeSigned(buffer, dimension);
        McpeUtil.writeBlockCoords(buffer, worldSpawn);
        buffer.writeBoolean(hasAchievementsDisabled);
        Varints.encodeSigned(buffer, dayCycleStopTime);
        buffer.writeBoolean(eduMode);
        buffer.writeFloat(rainLevel);
        buffer.writeFloat(lightingLevel);
        buffer.writeBoolean(enableCommands);
        buffer.writeBoolean(isTexturepacksRequired);
        McpeUtil.writeVarintLengthString(buffer, levelId);
        McpeUtil.writeVarintLengthString(buffer, worldName);
    }
}
