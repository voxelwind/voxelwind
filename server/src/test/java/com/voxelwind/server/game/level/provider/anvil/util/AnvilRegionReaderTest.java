package com.voxelwind.server.game.level.provider.anvil.util;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.io.InputStream;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class AnvilRegionReaderTest {
    @Test
    public void hasChunk() throws Exception {
        String path = getClass().getClassLoader().getResource("example_region.mca").getFile();
        if (SystemUtils.IS_OS_WINDOWS) {
            path = path.substring(1);
        }
        try (AnvilRegionReader reader = new AnvilRegionReader(Paths.get(path))) {
            assertTrue("chunk (14, 2) is missing", reader.hasChunk(14, 2));
        }
    }

    @Test
    public void readChunk() throws Exception {
        String path = getClass().getClassLoader().getResource("example_region.mca").getFile();
        if (SystemUtils.IS_OS_WINDOWS) {
            path = path.substring(1);
        }
        try (AnvilRegionReader reader = new AnvilRegionReader(Paths.get(path))) {
            assertTrue("chunk (14, 2) is missing", reader.hasChunk(14, 2));
            try (InputStream stream = reader.readChunk(14, 2)) {
                stream.read();
            }
        }
    }
}