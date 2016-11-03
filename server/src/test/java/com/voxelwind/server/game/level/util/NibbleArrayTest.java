package com.voxelwind.server.game.level.util;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class NibbleArrayTest {
    private static final int ARRAY_SIZE = 512;

    @Test
    public void selfTest() throws Exception {
        NibbleArray array = new NibbleArray(ARRAY_SIZE);
        Random random = new Random(1);
        byte[] randomNumbers = new byte[ARRAY_SIZE];
        for (int i = 0; i < randomNumbers.length; i++) {
            randomNumbers[i] = (byte) random.nextInt(16);
        }
        for (int i = 0; i < ARRAY_SIZE; i++) { // 16 - maximum amount that can be in a nibble
            array.set(i, randomNumbers[i]);
        }

        for (int i = 0; i < ARRAY_SIZE; i++) {
            assertEquals("Stored value is not valid", randomNumbers[i], array.get(i));
        }
    }
}