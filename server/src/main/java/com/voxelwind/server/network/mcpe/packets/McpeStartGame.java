package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import javax.xml.bind.DatatypeConverter;

@Data
public class McpeStartGame implements NetworkPackage {
    private static final byte[] UNKNOWN = DatatypeConverter.parseHexBinary("01010000000000000000000000");
    private int seed;
    private byte dimension;
    private int generator;
    private int gamemode;
    private long entityId;
    private Vector3i spawnLocation;
    private Vector3f position;

    @Override
    public void decode(ByteBuf buffer) {
        seed = buffer.readInt();
        dimension = buffer.readByte();
        generator = buffer.readInt();
        gamemode = buffer.readInt();
        entityId = buffer.readLong();
        spawnLocation = McpeUtil.readVector3i(buffer);
        position = McpeUtil.readVector3f(buffer);
        buffer.skipBytes(UNKNOWN.length);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(seed);
        buffer.writeByte(dimension);
        buffer.writeInt(generator);
        buffer.writeInt(gamemode);
        buffer.writeLong(entityId);
        McpeUtil.writeVector3i(buffer, spawnLocation, false);
        McpeUtil.writeVector3f(buffer, position);
        buffer.writeBytes(UNKNOWN);
    }
}
