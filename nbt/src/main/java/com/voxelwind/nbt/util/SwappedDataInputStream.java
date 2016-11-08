package com.voxelwind.nbt.util;

import java.io.*;

public class SwappedDataInputStream implements DataInput, Closeable {
    private final DataInputStream stream;

    public SwappedDataInputStream(InputStream stream) {
        this.stream = new DataInputStream(stream);
    }

    public SwappedDataInputStream(DataInputStream stream) {
        this.stream = stream;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        stream.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        stream.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return stream.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return stream.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return stream.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return stream.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return Short.reverseBytes(stream.readShort());
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return Integer.reverseBytes(stream.readUnsignedShort());
    }

    @Override
    public char readChar() throws IOException {
        return Character.reverseBytes(stream.readChar());
    }

    @Override
    public int readInt() throws IOException {
        return Integer.reverseBytes(stream.readInt());
    }

    @Override
    public long readLong() throws IOException {
        return Long.reverseBytes(stream.readLong());
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(Long.reverseBytes(stream.readLong()));
    }

    @Override
    public String readLine() throws IOException {
        return stream.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return stream.readUTF();
    }
}
