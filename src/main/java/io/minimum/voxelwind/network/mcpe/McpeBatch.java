package io.minimum.voxelwind.network.mcpe;

import io.minimum.voxelwind.network.Native;
import io.minimum.voxelwind.network.PacketRegistry;
import io.minimum.voxelwind.network.mcpe.annotations.BatchDisallowed;
import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import net.md_5.bungee.jni.zlib.BungeeZlib;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

@BatchDisallowed // You don't batch a batch packet, it makes no sense.
public class McpeBatch implements RakNetPackage {
    private static final ThreadLocal<BungeeZlib> inflaterLocal = new ThreadLocal<BungeeZlib>() {
        @Override
        protected net.md_5.bungee.jni.zlib.BungeeZlib initialValue() {
            BungeeZlib zlib = Native.zlib.newInstance();
            zlib.init(true, Deflater.DEFAULT_COMPRESSION);
            return zlib;
        }
    };
    private static final ThreadLocal<BungeeZlib> deflaterLocal = new ThreadLocal<BungeeZlib>() {
        @Override
        protected net.md_5.bungee.jni.zlib.BungeeZlib initialValue() {
            BungeeZlib zlib = Native.zlib.newInstance();
            zlib.init(false, Deflater.DEFAULT_COMPRESSION);
            return zlib;
        }
    };
    private final List<RakNetPackage> packages = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
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

            try {
                inflaterLocal.get().process(source, decompressed);
            } catch (DataFormatException e) {
                throw new RuntimeException("Unable to inflate batch data", e);
            }

            // Now process the decompressed result.
            while (decompressed.isReadable()) {
                int length = decompressed.readInt();
                ByteBuf data = decompressed.readSlice(length);

                RakNetPackage pkg = PacketRegistry.tryDecode(data);
                if (pkg != null && !(pkg instanceof McpeBatch))
                    packages.add(pkg);
            }
        } finally {
            if (source != null && source != buffer) {
                source.release();
            }
            decompressed.release();
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        ByteBuf destination = null;
        ByteBuf source = PooledByteBufAllocator.DEFAULT.directBuffer();

        try {
            if (!buffer.isDirect()) {
                // Not a direct buffer, work on a temporary direct buffer and then write the contents out.
                destination = PooledByteBufAllocator.DEFAULT.directBuffer();
            } else {
                destination = buffer;
            }

            for (RakNetPackage netPackage : packages) {
                ByteBuf encodedPackage = PacketRegistry.tryEncode(netPackage);
                source.writeInt(encodedPackage.writableBytes());
                source.writeBytes(encodedPackage);
                encodedPackage.release();
            }

            try {
                deflaterLocal.get().process(source, destination);
            } catch (DataFormatException e) {
                throw new RuntimeException("Unable to deflate batch data", e);
            }

            if (destination != buffer) {
                buffer.writeBytes(destination);
            }
        } finally {
            if (destination != null && destination != buffer) {
                source.release();
            }
            source.release();
        }
    }

    public List<RakNetPackage> getPackages() {
        return packages;
    }
}
