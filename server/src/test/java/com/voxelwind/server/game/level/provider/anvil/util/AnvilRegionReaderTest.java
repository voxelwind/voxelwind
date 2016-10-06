package com.voxelwind.server.game.level.provider.anvil.util;

import org.junit.Test;

import java.io.InputStream;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class AnvilRegionReaderTest {
    @Test
    public void hasChunk() throws Exception {
        String path = getClass().getClassLoader().getResource("example_region.mca").getFile();
        if (isWindows()) {
            path = path.substring(1);
        }
        try (AnvilRegionReader reader = new AnvilRegionReader(Paths.get(path))) {
            assertTrue("chunk (14, 2) is missing", reader.hasChunk(14, 2));
        }
    }

    @Test
    public void readChunk() throws Exception {
        String path = getClass().getClassLoader().getResource("example_region.mca").getFile();
        if (isWindows()) {
            path = path.substring(1);
        }
        try (AnvilRegionReader reader = new AnvilRegionReader(Paths.get(path))) {
            assertTrue("chunk (14, 2) is missing", reader.hasChunk(14, 2));
            try (InputStream stream = reader.readChunk(14, 2)) {
                stream.read();
            }
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}