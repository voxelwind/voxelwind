package com.voxelwind.api.game.item.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class CoalTest {
    @Test
    public void of() throws Exception {
        assertEquals(Coal.REGULAR, Coal.of((short) 0));
        assertEquals(Coal.CHARCOAL, Coal.of((short) 1));
    }

    @Test
    public void toMetadata() throws Exception {
        assertEquals(0, Coal.REGULAR.toMetadata());
        assertEquals(1, Coal.CHARCOAL.toMetadata());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOfInvalidMetadata() {
        Coal.of((short) 42);
    }
}