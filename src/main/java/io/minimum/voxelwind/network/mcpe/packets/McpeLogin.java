package io.minimum.voxelwind.network.mcpe.packets;

import io.minimum.voxelwind.network.mcpe.McpeUtil;
import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.zip.DataFormatException;

public class McpeLogin implements RakNetPackage {
    private int protocolVersion; // = 81
    private String chainData;
    private String skinData;

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getChainData() {
        return chainData;
    }

    public void setChainData(String chainData) {
        this.chainData = chainData;
    }

    public String getSkinData() {
        return skinData;
    }

    public void setSkinData(String skinData) {
        this.skinData = skinData;
    }

    @Override
    public void decode(ByteBuf buffer) {
        protocolVersion = buffer.readInt();
        int bodyLength = buffer.readInt();
        ByteBuf body = buffer.readSlice(bodyLength);

        // Decompress the body
        ByteBuf result = null;
        try {
            result = CompressionUtil.inflate(body);
            chainData = McpeUtil.readLELengthString(result);
            skinData = McpeUtil.readLELengthString(result);
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

        ByteBuf body = PooledByteBufAllocator.DEFAULT.buffer();
        try {
            McpeUtil.writeLELengthString(body, chainData);
            McpeUtil.writeLELengthString(body, skinData);

            ByteBuf compressed = CompressionUtil.deflate(body);

            buffer.writeInt(compressed.readableBytes());
            buffer.writeBytes(compressed);
        } catch (DataFormatException e) {
            throw new RuntimeException("Unable to compress login data body", e);
        } finally {
            body.release();
        }
    }
}
