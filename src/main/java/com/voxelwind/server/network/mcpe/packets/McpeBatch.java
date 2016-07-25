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
            System.out.println("[Before Decompress]\n" + buffer);

            int compressedSize = buffer.readInt() - 4; // skips Adler32
            decompressed = CompressionUtil.inflate(buffer.readSlice(compressedSize));
            System.out.println("[After Decompress]\n" + decompressed);

            // Now process the decompressed result.
            while (decompressed.isReadable()) {
                int length = (decompressed.readInt() & 0xFF); // WTF
                ByteBuf data = decompressed.readSlice(length);

                if (data.readableBytes() == 0) {
                    throw new DataFormatException("Contained batch packet is empty.");
                }

                System.out.println("[Decompressed]:\n" + ByteBufUtil.prettyHexDump(data));

                RakNetPackage pkg = PacketRegistry.tryDecode(data, PacketType.MCPE, true);
                if (pkg != null) {
                    packages.add(pkg);
                } else {
                    data.readerIndex(0);
                    McpeUnknown unknown = new McpeUnknown();
                    unknown.decode(data);
                    packages.add(unknown);
                }
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
                if (netPackage.getClass().isAnnotationPresent(BatchDisallowed.class)) {
                    throw new DataFormatException("Packet " + netPackage + " does not permit batching.");
                }
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
