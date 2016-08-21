package com.voxelwind.server.game.level.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class NibbleArrayTest {
    @Test
    public void selfTest() throws Exception {
        NibbleArray array = new NibbleArray(16);
        for (int i = 0; i < 16; i++) { // 16 - maximum amount that can be in a nibble
            array.set(i, (byte) i);
        }

        for (int i = 0; i < 16; i++) {
            assertEquals("Stored value is not valid", (byte) i, array.get(i));
        }
    }
}