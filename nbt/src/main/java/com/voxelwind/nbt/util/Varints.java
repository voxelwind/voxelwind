package com.voxelwind.nbt.util;

import io.netty.buffer.ByteBuf;

import java.io.*;
import java.nio.ByteBuffer;

public class Varints {
    private static final int MSB = 0x80
            , REST = 0x7F
            , MSBALL = ~REST;
    private static final long MSBALL_LONG = ~REST;

    public static void encodeUnsigned(int num, DataOutput output) throws IOException {
        while ((num & MSBALL) != 0) {
            output.writeByte((byte)((num & REST) | MSB));
            num >>= 7;
        }
        output.writeByte((byte) num);
    }

    public static void encodeUnsigned(int num, ByteBuf output) {
        while ((num & MSBALL) != 0) {
            output.writeByte((byte)((num & REST) | MSB));
            num >>= 7;
        }
        output.writeByte((byte) num);
    }

    public static void encodeUnsignedLong(long num, DataOutput output) throws IOException {
        while ((num & MSBALL_LONG) != 0) {
            output.writeByte((byte)((num & REST) | MSB));
            num >>= 7;
        }
        output.writeByte((byte) num);
    }

    public static void encodeUnsignedLong(long num, ByteBuf output) {
        while ((num & MSBALL_LONG) != 0) {
            output.writeByte((byte)((num & REST) | MSB));
            num >>>= 7;
        }
        output.writeByte((int) (num & REST));
    }

    public static int decodeUnsigned(DataInput input) throws IOException {
        int result = 0;
        int j = 0;
        int read;

        do {
            read = input.readByte();
            result |= (read & 0x7f) << j++ * 7;

            if (j > 5)
            {
                throw new IllegalArgumentException("VarInt too big");
            }
        } while ((read & 0x80) == 0x80);

        return result;
    }

    public static int decodeUnsigned(ByteBuf input) {
        int result = 0;
        int j = 0;
        int read;

        do {
            read = input.readByte();
            result |= (read & 0x7f) << j++ * 7;

            if (j > 5)
            {
                throw new IllegalArgumentException("VarInt too big");
            }
        } while ((read & 0x80) == 0x80);

        return result;
    }

    private static long decodeUnsignedLong(DataInput input) throws IOException {
        long result = 0;
        int j = 0;
        int read;

        do {
            read = input.readByte();
            result |= (read & 0x7f) << j++ * 7;

            if (j > 10)
            {
                throw new IllegalArgumentException("VarInt too big");
            }
        } while ((read & 0x80) == 0x80);

        return result;
    }

    private static long decodeUnsignedLong(ByteBuf input) {
        long result = 0;
        int j = 0;
        int read;

        do {
            read = input.readByte();
            result |= (read & 0x7f) << j++ * 7;

            if (j > 10)
            {
                throw new IllegalArgumentException("VarInt too big");
            }
        } while ((read & 0x80) == 0x80);

        return result;
    }

    public static void encodeSigned(int num, DataOutput output) throws IOException {
        encodeUnsigned((num << 1) ^ (num >> 31), output);
    }

    public static void encodeSigned(int num, ByteBuf output) {
        encodeUnsigned((num << 1) ^ (num >> 31), output);
    }

    public static void encodeSigned(long num, DataOutput output) throws IOException {
        encodeUnsignedLong((num << 1) ^ (num >> 63), output);
    }

    public static void encodeSigned(long num, ByteBuf output) {
        encodeUnsignedLong((num << 1) ^ (num >> 63), output);
    }

    public static int decodeSigned(DataInput input) throws IOException {
        int n = decodeUnsigned(input);
        return (n >> 1) ^ -(n & 1);
    }

    public static int decodeSigned(ByteBuf input) throws IOException {
        int n = decodeUnsigned(input);
        return (n >> 1) ^ -(n & 1);
    }

    public static long decodeSignedLong(DataInput input) throws IOException {
        long n = decodeUnsigned(input);
        return (n >> 1) ^ -(n & 1);
    }

    public static long decodeSignedLong(ByteBuf input) throws IOException {
        long n = decodeUnsigned(input);
        return (n >> 1) ^ -(n & 1);
    }
}
