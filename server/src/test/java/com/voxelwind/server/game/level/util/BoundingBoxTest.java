package com.voxelwind.server.game.level.util;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoundingBoxTest {
    @Test
    public void startAndEndCorrectionNotNeeded() throws Exception {
        Vector3f startNotCorrected = new Vector3f(0.5, 10, 9.5);
        Vector3f endNotCorrected = new Vector3f(90, 19, 28);
        BoundingBox box = new BoundingBox(startNotCorrected, endNotCorrected);
        assertEquals("start location not the same", startNotCorrected, box.getStart());
        assertEquals("end location not the same", endNotCorrected, box.getEnd());
    }

    @Test
    public void startAndEndCorrectionRequired() throws Exception {
        Vector3f startNotCorrected = new Vector3f(0.5, 10, 9.5);
        Vector3f endNotCorrected = new Vector3f(90, 19, 28);

        Vector3f startNeedsCorrection = new Vector3f(90, 19, 28);
        Vector3f endNeedsCorrection = new Vector3f(0.5, 10, 9.5);
        BoundingBox box2 = new BoundingBox(startNeedsCorrection, endNeedsCorrection);
        assertEquals("start location not the same", startNotCorrected, box2.getStart());
        assertEquals("end location not the same", endNotCorrected, box2.getEnd());
    }

    @Test
    public void negativeStartAndEndCorrectionNotNeeded() throws Exception {
        Vector3f startNotCorrected = new Vector3f(-32.1, 10, -582.8);
        Vector3f endNotCorrected = new Vector3f(90, 19, -28);
        BoundingBox box = new BoundingBox(startNotCorrected, endNotCorrected);
        assertEquals("start location not the same", startNotCorrected, box.getStart());
        assertEquals("end location not the same", endNotCorrected, box.getEnd());
    }

    @Test
    public void negativeStartAndEndCorrectionRequired() throws Exception {
        Vector3f startNotCorrected = new Vector3f(-32.1, 10, -582.8);
        Vector3f endNotCorrected = new Vector3f(90, 19, -28);

        Vector3f startNeedsCorrection = new Vector3f(90, 19, -582.8);
        Vector3f endNeedsCorrection = new Vector3f(-32.1, 10, -28);
        BoundingBox box2 = new BoundingBox(startNeedsCorrection, endNeedsCorrection);
        assertEquals("start location not the same", startNotCorrected, box2.getStart());
        assertEquals("end location not the same", endNotCorrected, box2.getEnd());
    }

    @Test
    public void isWithin() throws Exception {
        Vector3f startNotCorrected = new Vector3f(0.5, 10, 9.5);
        Vector3f endNotCorrected = new Vector3f(90, 19, 28);
        BoundingBox box = new BoundingBox(startNotCorrected, endNotCorrected);
        assertTrue("location that is in the box is not in the box", box.isWithin(new Vector3f(42.7, 11, 15.5)));
        assertFalse("location that is not in the box is in the box", box.isWithin(new Vector3f(162.7, 11, 15.5)));
        assertFalse("location that is not in the box is in the box", box.isWithin(new Vector3f(42.7, 20, 15.5)));
        assertFalse("location that is not in the box is in the box", box.isWithin(new Vector3f(42.7, 11, 55.5)));
        assertFalse("location that is not in the box is in the box", box.isWithin(new Vector3f(-42.7, 11, 55.5)));
        assertFalse("location that is not in the box is in the box", box.isWithin(new Vector3f(42.7, -11, 55.5)));
        assertFalse("location that is not in the box is in the box", box.isWithin(new Vector3f(42.7, 11, -55.5)));
    }

    @Test
    public void isWithinUsingNegative() throws Exception {
        Vector3f startNotCorrected = new Vector3f(-32.1, 10, -582.8);
        Vector3f endNotCorrected = new Vector3f(90, 19, -28);
        BoundingBox box = new BoundingBox(startNotCorrected, endNotCorrected);
        assertTrue("location that is in the box is not in the box", box.isWithin(new Vector3f(-12.7, 11, -153.5)));
        assertFalse("location that is not in the box is in the box", box.isWithin(new Vector3f(-162.7, 11, -153.5)));
        assertFalse("location that is not in the box is in the box", box.isWithin(new Vector3f(-12.7, -11, -153.5)));
        assertFalse("location that is not in the box is in the box", box.isWithin(new Vector3f(-12.7, 11, 1053.5)));
        assertFalse("location that is not in the box is in the box", box.isWithin(new Vector3f(-12.7, 11, -1053.5)));
    }
}