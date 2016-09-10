package com.voxelwind.server.game.level.util;

import com.google.common.base.Preconditions;

import java.util.Arrays;

public class NibbleArray {
    private final byte[] data;

    public NibbleArray(int length) {
        data = new byte[length / 2];
    }

    public NibbleArray(byte[] array) {
        data = array;
    }

    public byte get(int index) {
        Preconditions.checkElementIndex(index, data.length * 2);
        return (byte) (data[index / 2] >> ((index) % 2 * 4) & 0xF);
    }

    public void set(int index, byte value) {
        Preconditions.checkArgument(value >= 0 && value < 16, "Nibbles must have a value between 0 and 15.");
        Preconditions.checkElementIndex(index, data.length * 2);
        value &= 0xF;
        data[index / 2] &= (byte) (0xF << ((index + 1) % 2 * 4));
        data[index / 2] |= (byte) (value << (index % 2 * 4));
    }

    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    public NibbleArray copy() {
        return new NibbleArray(getData());
    }
}
