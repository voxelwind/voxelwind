package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.util.VersionUtil;
import com.voxelwind.server.network.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.AsciiString;
import lombok.Data;

import java.util.zip.DataFormatException;

@Data
public class McpeLogin implements NetworkPackage {
    private int protocolVersion;
    private byte gameEdition;
    private AsciiString chainData;
    private AsciiString skinData;

    @Override
    public void decode(ByteBuf buffer) {
        protocolVersion = buffer.readInt();
        if (!VersionUtil.isCompatible(protocolVersion)) {
            return;
        }
        gameEdition = buffer.readByte();
        int bodyLength = (int) Varints.decodeUnsigned(buffer);
        ByteBuf body = buffer.readSlice(bodyLength);

        // Decompress the body
        ByteBuf result = null;
        try {
            result = CompressionUtil.inflate(body);
            chainData = McpeUtil.readLELengthAsciiString(result);
            skinData = McpeUtil.readLELengthAsciiString(result);
        } catch (DataFormatException e) {
            throw new RuntimeException("Unable to inflate login data body", e);
        } finally {
            if (result != null) {
                result.release();
            }
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(protocolVersion);
        buffer.writeByte(gameEdition);

        ByteBuf body = PooledByteBufAllocator.DEFAULT.directBuffer();
        try {
            McpeUtil.writeLELengthAsciiString(body, chainData);
            McpeUtil.writeLELengthAsciiString(body, skinData);

            ByteBuf compressed = CompressionUtil.deflate(body);

            Varints.encodeUnsigned(buffer, compressed.readableBytes());
            buffer.writeBytes(compressed);
        } catch (DataFormatException e) {
            throw new RuntimeException("Unable to compress login data body", e);
        } finally {
            body.release();
        }
    }
}
