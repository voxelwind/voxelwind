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
            output.writeByte((num & 0xFF) | MSB);
            num >>>= 7;
        }
        output.writeByte(num & REST);
    }

    public static void encodeUnsigned(int num, ByteBuf output) {
        while ((num & MSBALL) != 0) {
            output.writeByte((num & 0xFF) | MSB);
            num >>>= 7;
        }
        output.writeByte(num & REST);
    }

    public static void encodeUnsignedLong(long num, DataOutput output) throws IOException {
        while ((num & MSBALL_LONG) != 0) {
            output.writeByte((int) ((num & 0xFF) | MSB));
            num >>>= 7;
        }
        output.writeByte((int) (num & REST));
    }

    public static void encodeUnsignedLong(long num, ByteBuf output) {
        while ((num & MSBALL_LONG) != 0) {
            output.writeByte((int) ((num & 0xFF) | MSB));
            num >>>= 7;
        }
        output.writeByte((int) (num & REST));
    }

    public static int decodeUnsigned(DataInput input) throws IOException {
        int res = 0, shift = 0, b;
        while (((b = input.readByte()) & MSB) == 0) {
            res += (b & REST) << shift;
            shift += 7;
            if (shift > 35) {
                throw new IllegalArgumentException("Provided VarInt is not valid.");
            }
        }
        return res;
    }

    public static int decodeUnsigned(ByteBuf input) throws IOException {
        int res = 0, shift = 0, b;
        while (((b = input.readByte()) & MSB) == 0) {
            res += (b & REST) << shift;
            shift += 7;
            if (shift > 35) {
                throw new IllegalArgumentException("Provided VarInt is not valid.");
            }
        }
        return res;
    }

    private static long decodeUnsignedLong(DataInput input) throws IOException {
        long res = 0, shift = 0, b;
        while (((b = input.readByte()) & MSB) == 0) {
            res += (b & REST) << shift;
            shift += 7;
            if (shift > 35) {
                throw new IllegalArgumentException("Provided VarLong is not valid.");
            }
        }
        return res;
    }

    private static long decodeUnsignedLong(ByteBuf input) throws IOException {
        long res = 0, shift = 0, b;
        while (((b = input.readByte()) & MSB) == 0) {
            res += (b & REST) << shift;
            shift += 7;
            if (shift > 35) {
                throw new IllegalArgumentException("Provided VarLong is not valid.");
            }
        }
        return res;
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
        return (n << 1) ^ (n >> 31);
    }

    public static int decodeSigned(ByteBuf input) throws IOException {
        int n = decodeUnsigned(input);
        return (n << 1) ^ (n >> 31);
    }

    public static long decodeSignedLong(DataInput input) throws IOException {
        long n = decodeUnsigned(input);
        return (n << 1) ^ (n >> 63);
    }

    public static long decodeSignedLong(ByteBuf input) throws IOException {
        long n = decodeUnsigned(input);
        return (n << 1) ^ (n >> 63);
    }
}
