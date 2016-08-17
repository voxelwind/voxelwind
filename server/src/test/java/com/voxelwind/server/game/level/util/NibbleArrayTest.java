package com.voxelwind.server.game.level.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class NibbleArrayTest {
    @Test
    public void selfTest() throws Exception {
        NibbleArray array = new NibbleArray(2);
        array.set(0, (byte) 1);
        array.set(1, (byte) 5);
        assertEquals(1, array.get(0));
        assertEquals(5, array.get(1));
    }
}