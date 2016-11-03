package com.voxelwind.api.game.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextFormatTest {
    private static final String NON_FORMATTED = "Voxelwind";
    private static final String FORMATTED = "\u00a7cVoxelwind";
    private static final String FORMATTED_MULTIPLE = "\u00a7c\u00a7lVoxelwind";
    private static final String FORMATTED_MULTIPLE_VARIED = "\u00a7c\u00a7lVoxel\u00a7a\u00a7mwind";
    private static final String INVALID = "\u00a7gVoxelwind";
    private static final String RAW_SECTION = "\u00a7";

    @Test
    public void removeFormattingNonFormatted() throws Exception {
        assertEquals(NON_FORMATTED, TextFormat.removeFormatting(NON_FORMATTED));
    }

    @Test
    public void removeFormattingFormatted() throws Exception {
        assertEquals(NON_FORMATTED, TextFormat.removeFormatting(FORMATTED));
    }

    @Test
    public void removeFormattingFormattedMultiple() throws Exception {
        assertEquals(NON_FORMATTED, TextFormat.removeFormatting(FORMATTED_MULTIPLE));
    }

    @Test
    public void removeFormattingFormattedMultipleVaried() throws Exception {
        assertEquals(NON_FORMATTED, TextFormat.removeFormatting(FORMATTED_MULTIPLE_VARIED));
    }

    @Test
    public void removeFormattingInvalidFormat() throws Exception {
        assertEquals(INVALID, TextFormat.removeFormatting(INVALID));
    }

    @Test
    public void removeFormattingRawSection() throws Exception {
        assertEquals(RAW_SECTION, TextFormat.removeFormatting(RAW_SECTION));
    }
}