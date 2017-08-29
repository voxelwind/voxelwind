package com.voxelwind.server.network.mcpe.packets;

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
    private int playerGamemode;
    private Vector3f spawn; // = null;
    private float pitch; // = null;
    private float yaw;
    private int seed; // = null;
    private int dimension; // = null;
    private int generator; // = null;
    private int worldGamemode; // = null;
    private int difficulty; // = null;
    private Vector3i worldSpawn; // = null;
    private boolean hasAchievementsDisabled; // = null;
    private int dayCycleStopTime; // = null;
    private boolean eduMode; // = null;
    private float rainLevel; // = null;
    private float lightingLevel; // = null;
    private boolean enableCommands; // = null;
    private boolean isTexturepacksRequired; // = null;
    // private GameRules gameRules; // TODO
    private String levelId; // = null;
    private String worldName; // = null;
    private String premiumWorldTemplateId;
    private boolean unknown0;
    private long currentTick;

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSignedLong(buffer, entityId);
        Varints.encodeUnsigned(buffer, runtimeEntityId);
        Varints.encodeSigned(buffer, playerGamemode);
        McpeUtil.writeVector3f(buffer, spawn);
        buffer.writeFloat(pitch);
        buffer.writeFloat(yaw);
        Varints.encodeSigned(buffer, seed);
        Varints.encodeSigned(buffer, dimension);
        Varints.encodeSigned(buffer, generator);
        Varints.encodeSigned(buffer, worldGamemode);
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
