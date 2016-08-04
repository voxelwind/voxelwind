package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

import javax.xml.bind.DatatypeConverter;

public class McpeStartGame implements RakNetPackage {
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

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public byte getDimension() {
        return dimension;
    }

    public void setDimension(byte dimension) {
        this.dimension = dimension;
    }

    public int getGenerator() {
        return generator;
    }

    public void setGenerator(int generator) {
        this.generator = generator;
    }

    public int getGamemode() {
        return gamemode;
    }

    public void setGamemode(int gamemode) {
        this.gamemode = gamemode;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public Vector3i getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Vector3i spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }
}
