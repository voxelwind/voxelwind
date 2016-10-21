package com.voxelwind.nbt.io;

import com.voxelwind.nbt.tags.*;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.voxelwind.nbt.io.NBTEncoding.MCPE_0_16_NETWORK;

public class NBTReader implements Closeable {
    private final DataInput input;
    private final NBTEncoding encoding;

    public NBTReader(DataInput input) {
        this(input, NBTEncoding.NOTCHIAN);
    }

    public NBTReader(DataInput input, NBTEncoding encoding) {
        this.input = Objects.requireNonNull(input, "input");
        this.encoding = Objects.requireNonNull(encoding, "encoding");
    }

    public Tag<?> readTag() throws IOException {
        int typeId = input.readByte() & 0xFF;
        TagType type = TagType.fromId(typeId);
        if (type == null) {
            throw new IOException("Invalid encoding ID " + typeId);
        }

        return deserialize(type, false);
    }

    private Tag<?> deserialize(TagType type, boolean skipName) throws IOException {
        String tagName = null;
        if (type != TagType.END && !skipName) {
            int length = encoding == MCPE_0_16_NETWORK ? input.readByte() & 0xFF : input.readShort();
            byte[] tagNameBytes = new byte[length];
            input.readFully(tagNameBytes);
            tagName = new String(tagNameBytes, StandardCharsets.UTF_8);
        }

        switch (type) {
            case END:
                return EndTag.INSTANCE;
            case BYTE:
                byte b = input.readByte();
                return new ByteTag(tagName, b);
            case SHORT:
                short sh = input.readShort();
                return new ShortTag(tagName, sh);
            case STRING:
                int length = encoding == MCPE_0_16_NETWORK ? input.readByte() & 0xFF : input.readUnsignedShort();
                byte[] valueBytes = new byte[length];
                input.readFully(valueBytes);
                return new StringTag(tagName, new String(valueBytes, StandardCharsets.UTF_8));
            case COMPOUND:
                Map<String, Tag<?>> map = new HashMap<>();
                Tag<?> inTag1;
                while ((inTag1 = readTag()) != EndTag.INSTANCE) {
                    map.put(inTag1.getName(), inTag1);
                }
                return new CompoundTag(tagName, map);
        }

        throw new IllegalArgumentException("Unknown type " + type);
    }

    @Override
    public void close() throws IOException {
        if (input instanceof Closeable) {
            ((Closeable) input).close();
        }
    }
}
