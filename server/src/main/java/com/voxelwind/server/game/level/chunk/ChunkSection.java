package com.voxelwind.server.game.level.chunk;

import com.google.common.base.Preconditions;
import com.voxelwind.server.game.level.util.NibbleArray;

import java.util.Arrays;

/**
 * Represents a 16x16x16 section of a chunk (which is 16x256x16).
 */
public class ChunkSection {
    private static final int SECTION_SIZE = 4096;
    private final byte[] ids;
    private final NibbleArray data;
    private final NibbleArray skyLight;
    private final NibbleArray blockLight;

    public ChunkSection() {
        this.ids = new byte[SECTION_SIZE];
        this.data = new NibbleArray(SECTION_SIZE);
        this.skyLight = new NibbleArray(SECTION_SIZE);
        this.blockLight = new NibbleArray(SECTION_SIZE);
    }

    public ChunkSection(byte[] ids, NibbleArray data, NibbleArray skyLight, NibbleArray blockLight) {
        this.ids = ids;
        this.data = data;
        this.skyLight = skyLight;
        this.blockLight = blockLight;
    }

    public int getBlockId(int x, int y, int z) {
        checkBounds(x, y, z);
        return ids[anvilBlockPosition(x, y, z)] & 0xff;
    }

    public byte getBlockData(int x, int y, int z) {
        checkBounds(x, y, z);
        return data.get(anvilBlockPosition(x, y, z));
    }

    public byte getSkyLight(int x, int y, int z) {
        checkBounds(x, y, z);
        return skyLight.get(anvilBlockPosition(x, y, z));
    }

    public byte getBlockLight(int x, int y, int z) {
        checkBounds(x, y, z);
        return blockLight.get(anvilBlockPosition(x, y, z));
    }

    public void setBlockId(int x, int y, int z, byte id) {
        checkBounds(x, y, z);
        ids[anvilBlockPosition(x, y, z)] = id;
    }

    public void setBlockData(int x, int y, int z, byte data) {
        checkBounds(x, y, z);
        this.data.set(anvilBlockPosition(x, y, z), data);
    }

    public void setSkyLight(int x, int y, int z, byte val) {
        checkBounds(x, y, z);
        skyLight.set(anvilBlockPosition(x, y, z), val);
    }

    public void setBlockLight(int x, int y, int z, byte val) {
        checkBounds(x, y, z);
        blockLight.set(anvilBlockPosition(x, y, z), val);
    }

    public byte[] getIds() {
        return ids;
    }

    public NibbleArray getData() {
        return data;
    }

    public NibbleArray getSkyLight() {
        return skyLight;
    }

    public NibbleArray getBlockLight() {
        return blockLight;
    }

    public ChunkSection copy() {
        return new ChunkSection(
                ids.clone(),
                data.copy(),
                skyLight.copy(),
                blockLight.copy()
        );
    }

    public boolean isEmpty() {
        for (byte id : ids) {
            if (id != 0) {
                return false;
            }
        }
        return true;
    }

    private static void checkBounds(int x, int y, int z) {
        Preconditions.checkArgument(x >= 0 && x < 16, "x (%s) is not between 0 and 15", x);
        Preconditions.checkArgument(y >= 0 && y < 16, "y (%s) is not between 0 and 15", x);
        Preconditions.checkArgument(z >= 0 && z < 16, "z (%s) is not between 0 and 15", x);
    }

    private static int anvilBlockPosition(int x, int y, int z) {
        return (x * 256) + (z * 16) + y;
    }
}
