package com.voxelwind.server.game.level.chunk.provider.anvil.util;

import com.voxelwind.nbt.io.NBTReader;
import com.voxelwind.nbt.io.NBTReaders;
import com.voxelwind.nbt.tags.CompoundTag;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.Deflater;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class AnvilRegionFileTest {
    @Test
    public void hasChunk() throws Exception {
        String path = getClass().getClassLoader().getResource("example_region.mca").getFile();
        if (isWindows()) {
            path = path.substring(1);
        }
        try (AnvilRegionFile reader = new AnvilRegionFile(Paths.get(path))) {
            assertTrue("chunk (14, 2) is missing", reader.hasChunk(14, 2));
        }
    }

    @Test
    public void readChunk() throws Exception {
        String path = getClass().getClassLoader().getResource("example_region.mca").getFile();
        if (isWindows()) {
            path = path.substring(1);
        }
        try (AnvilRegionFile file = new AnvilRegionFile(Paths.get(path))) {
            assertTrue("chunk (14, 2) is missing", file.hasChunk(14, 2));
            try (NBTReader reader1 = NBTReaders.createBigEndianReader(file.readChunk(14, 2))) {
                CompoundTag tag = (CompoundTag) reader1.readTag();
                assertTrue("Loaded chunk contains no 'Level' key", tag.getValue().containsKey("Level"));
            }
        }
    }

    @Test
    public void simpleWriteAndVerifyChunk() throws Exception {
        Path temporaryFile = Files.createTempFile("voxelwind-test-anvil-", ".mca");
        // Generate 4KB of random data
        byte[] randomData = new byte[4096];
        new Random(1).nextBytes(randomData);

        // Now compress it.
        ByteBuffer compressed = compress(randomData);

        try (AnvilRegionFile file = new AnvilRegionFile(temporaryFile)) {
            file.writeChunk(0, 0, compressed);

            byte[] reread = IOUtils.toByteArray(file.readChunk(0, 0));
            assertArrayEquals("Written data doesn't match the original", randomData, reread);
        }
    }

    @Test
    public void complexWriteAndVerifyChunk() throws Exception {
        Path temporaryFile = Files.createTempFile("voxelwind-test-anvil-", ".mca");
        // Generate some random data.
        Random random = new Random(1);
        byte[] randomData1 = new byte[4096];
        byte[] randomData2 = new byte[2048];
        byte[] randomData3 = new byte[2048];
        random.nextBytes(randomData1);
        random.nextBytes(randomData2);
        random.nextBytes(randomData3);

        // Now compress these buffers.
        ByteBuffer compressed1 = compress(randomData1);
        ByteBuffer compressed2 = compress(randomData2);
        ByteBuffer compressed3 = compress(randomData3);

        try (AnvilRegionFile file = new AnvilRegionFile(temporaryFile)) {
            // This allocates two sectors.
            file.writeChunk(0, 0, compressed1);

            // This will clear one sector.
            file.writeChunk(0, 0, compressed2);

            // This will occupy the just-cleared sector.
            file.writeChunk(0, 1, compressed3);

            byte[] reread1 = IOUtils.toByteArray(file.readChunk(0, 0));
            assertArrayEquals("Written data doesn't match the original", randomData2, reread1);
            byte[] reread2 = IOUtils.toByteArray(file.readChunk(0, 1));
            assertArrayEquals("Written data doesn't match the original", randomData3, reread2);
        }
    }

    @Test
    public void manipulateExistingFile() throws Exception {
        String originalPath = getClass().getClassLoader().getResource("example_region.mca").getFile();
        if (isWindows()) {
            originalPath = originalPath.substring(1);
        }
        Path temporaryFile = Files.createTempFile("voxelwind-test-anvil-", ".mca");
        Files.copy(Paths.get(originalPath), temporaryFile, StandardCopyOption.REPLACE_EXISTING);
        try (AnvilRegionFile file = new AnvilRegionFile(temporaryFile)) {
            // Generate 8KB of random data
            byte[] randomData1 = new byte[4096];
            new Random(1).nextBytes(randomData1);
            byte[] randomData2 = new byte[2048];
            new Random(2).nextBytes(randomData2);
            byte[] randomData3 = new byte[2048];
            new Random(3).nextBytes(randomData3);

            ByteBuffer compressed1 = compress(randomData1);
            ByteBuffer compressed2 = compress(randomData2);
            ByteBuffer compressed3 = compress(randomData3);
            file.writeChunk(14, 2, compressed1); // 14,2 exists in the region and its sectors will be cleared
            file.writeChunk(0, 7, compressed2); // 0,7 doesn't exist in the region and will be created
            file.writeChunk(5, 24, compressed3); // 5,24 exists in the region and its single sector will be overwritten

            byte[] reread1 = IOUtils.toByteArray(file.readChunk(14, 2));
            assertArrayEquals("Written data doesn't match the original", randomData1, reread1);
            byte[] reread2 = IOUtils.toByteArray(file.readChunk(0, 7));
            assertArrayEquals("Written data doesn't match the original", randomData2, reread2);
            byte[] reread3 = IOUtils.toByteArray(file.readChunk(5, 24));
            assertArrayEquals("Written data doesn't match the original", randomData3, reread3);
        }
    }

    private static ByteBuffer compress(byte[] data) {
        List<ByteBuffer> compressed = new ArrayList<>();
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        int totalLength = 0;
        while (!deflater.finished()) {
            byte[] bufferData = new byte[1024];
            int deflatedLength = deflater.deflate(bufferData);
            totalLength += deflatedLength;
            compressed.add(ByteBuffer.wrap(bufferData, 0, deflatedLength));
        }

        ByteBuffer finalized = ByteBuffer.allocate(totalLength);
        for (ByteBuffer buffer : compressed) {
            finalized.put(buffer);
        }
        finalized.flip();
        return finalized;
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}