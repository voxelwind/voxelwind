package io.minimum.voxelwind.network.mcpe.packets;

import io.minimum.voxelwind.network.PacketRegistry;
import io.minimum.voxelwind.network.PacketType;
import io.minimum.voxelwind.network.mcpe.annotations.BatchDisallowed;
import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.DataFormatException;

@BatchDisallowed // You don't batch a batch packet, it makes no sense.
public class McpeBatch implements RakNetPackage {
    private final List<RakNetPackage> packages = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        ByteBuf decompressed = null;
        try {
            int compressedSize = buffer.readInt();
            decompressed = CompressionUtil.inflate(buffer.readSlice(compressedSize));

            if (decompressed.readByte() != 0x78) {
                throw new DataFormatException("Found invalid zlib header byte (should be 0x78)");
            }

            decompressed.readByte();

            // Now process the decompressed result.
            System.out.println("First 512 bytes:");
            System.out.println(ByteBufUtil.prettyHexDump(decompressed, 0, 512));

            while (decompressed.isReadable()) {
                int length = decompressed.readInt();
                ByteBuf data = decompressed.readSlice(length);

                RakNetPackage pkg = PacketRegistry.tryDecode(data, PacketType.MCPE);
                if (pkg != null && !(pkg instanceof McpeBatch))
                    packages.add(pkg);
            }
        } catch (DataFormatException e) {
            throw new RuntimeException("Unable to inflate batch data", e);
        } finally {
            if (decompressed != null) {
                decompressed.release();
            }
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        ByteBuf source = PooledByteBufAllocator.DEFAULT.directBuffer();

        try {
            // Voxelwind uses default compression
            source.writeByte(0x78);
            source.writeByte(0x9c);

            for (RakNetPackage netPackage : packages) {
                ByteBuf encodedPackage = PacketRegistry.tryEncode(netPackage);
                source.writeInt(encodedPackage.readableBytes());
                source.writeBytes(encodedPackage);
                encodedPackage.release();
            }

            int originalWriterIndex = buffer.writerIndex();
            long adler = CompressionUtil.deflate(source, buffer);
            buffer.writeLong(adler);
        } catch (DataFormatException e) {
            throw new RuntimeException("Unable to deflate batch data", e);
        } finally {
            source.release();
        }
    }

    public List<RakNetPackage> getPackages() {
        return packages;
    }
}
