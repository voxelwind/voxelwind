package com.voxelwind.server.level.util;

public class NibbleArray {
    private final byte[] data;

    public NibbleArray(int length) {
        data = new byte[length / 2];
    }

    public NibbleArray(byte[] array) {
        data = array;
    }

    public byte get(int index) {
        return (byte) (data[index / 2] >> ((index) % 2 * 4) & 0xF);
    }

    public void set(int index, byte value) {
        value &= 0xF;
        data[index / 2] &= (byte) (0xF << ((index + 1) % 2 * 4));
        data[index / 2] |= (byte) (value << (index % 2 * 4));
    }

    public byte[] getData() {
        return data;
    }
}
