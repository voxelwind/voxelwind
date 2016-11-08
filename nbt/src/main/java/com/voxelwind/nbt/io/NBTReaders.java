package com.voxelwind.nbt.io;

import com.voxelwind.nbt.util.SwappedDataInputStream;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Objects;

public class NBTReaders {
    public static NBTReader createLittleEndianReader(InputStream stream) {
        Objects.requireNonNull(stream, "stream");
        return new NBTReader(new SwappedDataInputStream(stream));
    }

    public static NBTReader createBigEndianReader(InputStream stream) {
        Objects.requireNonNull(stream, "stream");
        return new NBTReader(new DataInputStream(stream));
    }
}
