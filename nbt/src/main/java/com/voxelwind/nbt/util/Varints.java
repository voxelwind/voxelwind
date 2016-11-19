package com.voxelwind.nbt.util;

import io.netty.buffer.ByteBuf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Varints {
    public static void encodeUnsigned(DataOutput output, long value) throws IOException {
        while (true) {
            if ((value & ~0x7FL) == 0) {
                output.writeByte((int) value);
                return;
            } else {
                output.writeByte((byte) (((int) value & 0x7F) | 0x80));
                value >>>= 7;
            }
        }
    }

    public static void encodeUnsigned(ByteBuf output, long value) {
        while (true) {
            if ((value & ~0x7FL) == 0) {
                output.writeByte((int) value);
                return;
            } else {
                output.writeByte((byte) (((int) value & 0x7F) | 0x80));
                value >>>= 7;
            }
        }
    }

    public static long decodeUnsigned(DataInput input) throws IOException {
        long result = 0;
        for (int shift = 0; shift < 64; shift += 7) {
            final byte b = input.readByte();
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
        }
        throw new RuntimeException("VarInt too large");
    }

    public static long decodeUnsigned(ByteBuf input) {
        long result = 0;
        for (int shift = 0; shift < 64; shift += 7) {
            final byte b = input.readByte();
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
        }
        throw new RuntimeException("VarInt too large");
    }

    public static void encodeSigned(DataOutput output, int num) throws IOException {
        encodeUnsigned(output, (num << 1) ^ (num >> 31));
    }

    public static void encodeSigned(ByteBuf output, int num) {
        encodeUnsigned(output, (num << 1) ^ (num >> 31));
    }

    public static void encodeSignedLong(DataOutput output, long num) throws IOException {
        encodeUnsigned(output, (num << 1) ^ (num >> 63));
    }

    public static void encodeSignedLong(ByteBuf output, long num) {
        encodeUnsigned(output, (num << 1) ^ (num >> 63));
    }

    public static int decodeSigned(DataInput input) throws IOException {
        int n = (int) decodeUnsigned(input);
        return (n >>> 1) ^ -(n & 1);
    }

    public static int decodeSigned(ByteBuf input) {
        int n = (int) decodeUnsigned(input);
        return (n >>> 1) ^ -(n & 1);
    }

    public static long decodeSignedLong(DataInput input) throws IOException {
        long n = decodeUnsigned(input);
        return (n >>> 1) ^ -(n & 1);
    }

    public static long decodeSignedLong(ByteBuf input) {
        long n = decodeUnsigned(input);
        return (n >>> 1) ^ -(n & 1);
    }
}
