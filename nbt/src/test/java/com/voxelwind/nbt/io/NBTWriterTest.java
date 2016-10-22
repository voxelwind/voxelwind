package com.voxelwind.nbt.io;

import com.voxelwind.nbt.tags.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.*;

public class NBTWriterTest {
    @Test
    public void simpleSelfConsistencyTestNotchian() throws Exception {
        Map<String, Tag<?>> compoundMap = new HashMap<>();
        compoundMap.put("name", new StringTag("name", "voxelwind-nbt"));
        CompoundTag writtenTag = new CompoundTag("Test file", compoundMap);
        selfVerify(writtenTag, NBTEncoding.NOTCHIAN);
    }

    @Test
    public void simpleSelfConsistencyTestMcpe016Network() throws Exception {
        Map<String, Tag<?>> compoundMap = new HashMap<>();
        compoundMap.put("name", new StringTag("name", "voxelwind-nbt"));
        CompoundTag writtenTag = new CompoundTag("Test file", compoundMap);
        selfVerify(writtenTag, NBTEncoding.MCPE_0_16_NETWORK);
    }

    @Test
    public void everyTagSelfConsistencyTestNotchian() throws Exception {
        CompoundTag cornucopia = generateCornucopia();
        selfVerify(cornucopia, NBTEncoding.NOTCHIAN);
    }

    @Test
    public void everyTagSelfConsistencyTestMcpe016Network() throws Exception {
        CompoundTag cornucopia = generateCornucopia();
        selfVerify(cornucopia, NBTEncoding.MCPE_0_16_NETWORK);
    }

    private CompoundTag generateCornucopia() {
        List<Tag<?>> allTags = new ArrayList<>();
        Random random = new Random(1);
        // String
        allTags.add(new StringTag("name", "voxelwind-nbt"));

        // Numbers
        allTags.add(new IntTag("answer to the life, the universe, and everything", 42));
        allTags.add(new FloatTag("change", 1.83f));
        allTags.add(new DoubleTag("total", 18.72f));
        allTags.add(new LongTag("a huge number", random.nextLong()));
        allTags.add(new LongTag("a smaller but still huge number", random.nextInt()));
        allTags.add(new ShortTag("dogs", (short) 32767));
        allTags.add(new ByteTag("bottles of beer", (byte) 99));

        // List and Compound
        List<StringTag> stringTags = new ArrayList<>();
        stringTags.add(new StringTag(null, "world peace"));
        stringTags.add(new StringTag(null, "O(1) internet"));
        stringTags.add(new StringTag(null, "intrepid space explorers"));
        allTags.add(new ListTag<>("pipe dreams", StringTag.class, stringTags));

        // Arrays
        byte[] bytes = new byte[100];
        random.nextBytes(bytes);
        int[] ints = new int[100];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = random.nextInt();
        }
        allTags.add(new ByteArrayTag("byteArray", bytes));
        allTags.add(new IntArrayTag("intArray", ints));

        return CompoundTag.createFromList("Cornucopia of magic", allTags);
    }

    private void selfVerify(Tag<?> tag, NBTEncoding encoding) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos); NBTWriter writer = new NBTWriter(dos, encoding)) {
            writer.write(tag);
            dos.flush();
        }

        Tag<?> readTag;
        try (NBTReader reader = new NBTReader(new DataInputStream(new ByteArrayInputStream(baos.toByteArray())), encoding)) {
            readTag = reader.readTag();
        }

        Assert.assertEquals(tag, readTag);
    }
}
