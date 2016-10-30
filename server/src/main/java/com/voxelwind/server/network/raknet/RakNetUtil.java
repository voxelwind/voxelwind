package com.voxelwind.server.network.raknet;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.voxelwind.server.network.raknet.RakNetConstants.RAKNET_UNCONNECTED_MAGIC;

@UtilityClass
public class RakNetUtil {
    public static void writeString(ByteBuf buffer, String string) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(string, "string");
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(bytes.length);
        buffer.writeBytes(bytes);
    }

    public static String readString(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");

        int length = buffer.readShort();
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void verifyUnconnectedMagic(ByteBuf buf) {
        byte[] readMagic = new byte[RAKNET_UNCONNECTED_MAGIC.length];
        buf.readBytes(readMagic);

        if (!Arrays.equals(readMagic, RAKNET_UNCONNECTED_MAGIC)) {
            throw new RuntimeException("Invalid packet magic.");
        }
    }

    public static InetAddress readAddress(ByteBuf buf) {
        short type = buf.readUnsignedByte();
        if (type == 4) {
            byte[] addr = new byte[4];
            buf.readBytes(addr);
            try {
                return InetAddress.getByAddress(addr);
            } catch (UnknownHostException e) {
                // ;_;
                throw new IllegalArgumentException(e);
            }
        } else {
            throw new UnsupportedOperationException("Can't deserialize an IPv6 address.");
        }
    }

    public static InetSocketAddress readSocketAddress(ByteBuf buf) {
        InetAddress address = readAddress(buf);
        int port = buf.readUnsignedShort();
        return new InetSocketAddress(address, port);
    }

    public static void writeAddress(ByteBuf buf, InetAddress address) {
        if (address instanceof Inet4Address) {
            buf.writeByte((4 & 0xFF));
            buf.writeBytes(address.getAddress());
        } else {
            throw new UnsupportedOperationException("Can't serialize an IPv6 address.");
        }
    }

    public static void writeSocketAddress(ByteBuf buf, InetSocketAddress address) {
        writeAddress(buf, address.getAddress());
        buf.writeShort(address.getPort());
    }
}
