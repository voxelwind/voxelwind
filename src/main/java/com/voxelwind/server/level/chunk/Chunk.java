package com.voxelwind.server.level.chunk;

import com.voxelwind.server.level.util.NibbleArray;

import java.awt.*;
import java.util.Arrays;

public class Chunk {
    private static final int FULL_CHUNK_SIZE = 16 * 16 * 128; // 32768

    private final NibbleArray blockData = new NibbleArray(FULL_CHUNK_SIZE);
    private final NibbleArray blockMetadata = new NibbleArray(FULL_CHUNK_SIZE);
    private final NibbleArray lightData = new NibbleArray(FULL_CHUNK_SIZE);

    private final int x;
    private final int z;
    private final byte[] biomeId = new byte[256];
    private final int[] biomeColor = new int[256];
    private final byte[] height = new byte[256];

    public Chunk(int x, int z) {
        this.x = x;
        this.z = z;
        Arrays.fill(biomeId, (byte) 1);
        Arrays.fill(biomeColor, Color.GREEN.getRGB());
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
}
