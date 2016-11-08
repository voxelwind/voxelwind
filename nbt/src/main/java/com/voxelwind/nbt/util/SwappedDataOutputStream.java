package com.voxelwind.nbt.util;

import java.io.*;

public class SwappedDataOutputStream implements DataOutput, Closeable {
    private final DataOutputStream stream;

    public SwappedDataOutputStream(OutputStream stream) {
        this.stream = new DataOutputStream(stream);
    }

    public SwappedDataOutputStream(DataOutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void write(int b) throws IOException {
        stream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        stream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        stream.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        stream.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        stream.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        stream.writeShort(Integer.reverseBytes(v) >> 16);
    }

    @Override
    public void writeChar(int v) throws IOException {
        stream.writeChar(Character.reverseBytes((char) v));
    }

    @Override
    public void writeInt(int v) throws IOException {
        stream.writeInt(Integer.reverseBytes(v));
    }

    @Override
    public void writeLong(long v) throws IOException {
        stream.writeLong(Long.reverseBytes(v));
    }

    @Override
    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    @Override
    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    @Override
    public void writeBytes(String s) throws IOException {
        stream.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        stream.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        stream.writeUTF(s);
    }
}
