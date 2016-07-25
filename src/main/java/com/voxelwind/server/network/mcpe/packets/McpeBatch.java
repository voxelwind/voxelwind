package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.PacketRegistry;
import com.voxelwind.server.network.PacketType;
import com.voxelwind.server.network.mcpe.annotations.BatchDisallowed;
import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

@BatchDisallowed // You don't batch a batch packet, it makes no sense.
public class McpeBatch implements RakNetPackage {
    private final List<RakNetPackage> packages = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        ByteBuf decompressed = null;
        try {
            int compressedSize = buffer.readInt() - 4; // skips Adler32
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

            // Write a temporary size here. We'll replace it later.
            int lengthPosition = buffer.writerIndex();
            buffer.writeInt(0);

            // Compress the buffer
            int afterLength = buffer.writerIndex();
            int adler = CompressionUtil.deflate(source, buffer);

            // Replace the dummy length we wrote
            buffer.setInt(lengthPosition, buffer.writerIndex() - afterLength);

            // Write Adler32 checksum
            buffer.writeInt(adler);
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
