package com.voxelwind.server.network.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import net.md_5.bungee.jni.zlib.BungeeZlib;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

public class CompressionUtil {
    private static final ThreadLocal<BungeeZlib> inflaterLocal = new ThreadLocal<BungeeZlib>() {
        @Override
        protected net.md_5.bungee.jni.zlib.BungeeZlib initialValue() {
            BungeeZlib zlib = NativeCodeFactory.zlib.newInstance();
            zlib.init(false, Deflater.DEFAULT_COMPRESSION);
            return zlib;
        }
    };
    private static final ThreadLocal<BungeeZlib> deflaterLocal = new ThreadLocal<BungeeZlib>() {
        @Override
        protected net.md_5.bungee.jni.zlib.BungeeZlib initialValue() {
            BungeeZlib zlib = NativeCodeFactory.zlib.newInstance();
            zlib.init(true, Deflater.DEFAULT_COMPRESSION);
            return zlib;
        }
    };

    private CompressionUtil() {

    }

    /**
     * Decompresses a buffer.
     *
     * @param buffer the buffer to decompress
     * @return the decompressed buffer
     * @throws DataFormatException if data could not be inflated
     */
    public static ByteBuf inflate(ByteBuf buffer) throws DataFormatException {
        // Ensure that this buffer is direct.
        ByteBuf source = null;
        ByteBuf decompressed = PooledByteBufAllocator.DEFAULT.directBuffer();

        try {
            if (!buffer.isDirect()) {
                // We don't have a direct buffer. Create one.
                ByteBuf temporary = PooledByteBufAllocator.DEFAULT.directBuffer();
                temporary.writeBytes(buffer);
                source = temporary;
            } else {
                source = buffer;
            }

            inflaterLocal.get().process(source, decompressed);
            return decompressed;
        } catch (DataFormatException e) {
            decompressed.release();
            throw e;
        } finally {
            if (source != null && source != buffer) {
                source.release();
            }
        }
    }

    /**
     * Compresses a buffer.
     *
     * @param buffer the buffer to compress
     * @return a new compressed buffer
     * @throws DataFormatException if data could not be deflated
     */
    public static ByteBuf deflate(ByteBuf buffer) throws DataFormatException {
        ByteBuf dest = PooledByteBufAllocator.DEFAULT.directBuffer();
        try {
            deflate(buffer, dest);
        } catch (DataFormatException e) {
            dest.release();
            throw e;
        }
        return dest;
    }

    /**
     * Compresses a {@link ByteBuf}.
     *
     * @param toCompress the buffer to compress
     * @param into       the buffer to compress into
     * @throws DataFormatException if data could not be deflated
     */
    public static void deflate(ByteBuf toCompress, ByteBuf into) throws DataFormatException {
        ByteBuf destination = null;
        ByteBuf source = null;

        try {
            if (!toCompress.isDirect()) {
                // Source is not a direct buffer. Work on a temporary direct buffer and then write the contents out.
                source = PooledByteBufAllocator.DEFAULT.directBuffer();
                source.writeBytes(toCompress);
            } else {
                source = toCompress;
            }

            if (!into.isDirect()) {
                // Destination is not a direct buffer. Work on a temporary direct buffer and then write the contents out.
                destination = PooledByteBufAllocator.DEFAULT.directBuffer();
            } else {
                destination = into;
            }

            deflaterLocal.get().process(source, destination);

            if (destination != into) {
                into.writeBytes(destination);
            }
        } finally {
            if (source != null && source != toCompress) {
                source.release();
            }
            if (destination != null && destination != into) {
                destination.release();
            }
        }
    }
}
