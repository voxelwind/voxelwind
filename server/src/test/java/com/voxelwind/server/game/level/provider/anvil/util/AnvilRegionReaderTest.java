package com.voxelwind.server.game.level.provider.anvil.util;

import com.voxelwind.nbt.io.NBTReader;
import com.voxelwind.nbt.io.NBTReaders;
import com.voxelwind.nbt.tags.CompoundTag;
import org.junit.Test;

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
            try (NBTReader reader1 = NBTReaders.createBigEndianReader(reader.readChunk(14, 2))) {
                CompoundTag tag = (CompoundTag) reader1.readTag();
                assertTrue("Loaded chunk contains no 'Level' key", tag.getValue().containsKey("Level"));
            }
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}