package io.minimum.voxelwind.network.mcpe.packets;

import io.minimum.voxelwind.network.PacketRegistry;
import io.minimum.voxelwind.network.PacketType;
import io.minimum.voxelwind.network.mcpe.annotations.BatchDisallowed;
import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.ArrayList;
import java.util.List;

@BatchDisallowed // You don't batch a batch packet, it makes no sense.
public class McpeBatch implements RakNetPackage {
    private final List<RakNetPackage> packages = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        // Ensure that this buffer is direct.
        ByteBuf decompressed = null;
        try {
            decompressed = CompressionUtil.inflate(buffer);

            // Now process the decompressed result.
            while (decompressed.isReadable()) {
                int length = decompressed.readInt();
                ByteBuf data = decompressed.readSlice(length);

                RakNetPackage pkg = PacketRegistry.tryDecode(data, PacketType.MCPE);
                if (pkg != null && !(pkg instanceof McpeBatch))
                    packages.add(pkg);
            }
        } finally {
            if (decompressed != null) {
                decompressed.release();
            }
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
                ByteBuf encodedPackage = PacketRegistry.tryEncode(netPackage, PacketType.MCPE);
                source.writeInt(encodedPackage.readableBytes());
                source.writeBytes(encodedPackage);
                encodedPackage.release();
            }

            CompressionUtil.deflate(source, buffer);
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
