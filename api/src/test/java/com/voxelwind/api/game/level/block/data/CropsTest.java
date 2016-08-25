package com.voxelwind.api.game.level.block.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class CropsTest {
    @Test
    public void ofStage() throws Exception {
        assertEquals(Crops.NEW, Crops.ofStage(0));
        assertEquals(Crops.FULLY_GROWN, Crops.ofStage(7));
    }

    @Test
    public void isFullyGrown() throws Exception {
        for (int i = 0; i < 7; i++) {
            assertFalse(Crops.ofStage(i).isFullyGrown());
        }
        assertTrue(Crops.ofStage(7).isFullyGrown());
    }

}