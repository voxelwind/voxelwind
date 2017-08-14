package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.util.VersionUtil;
import com.voxelwind.server.network.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.AsciiString;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.zip.DataFormatException;

@Data
public class McpeLogin implements NetworkPackage {
    private static final Logger LOGGER = LogManager.getLogger(McpeLogin.class);

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

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[MCPE LOGIN HEX]\n{}", ByteBufUtil.prettyHexDump(body));
        }

        chainData = McpeUtil.readLELengthAsciiString(body);
        skinData = McpeUtil.readLELengthAsciiString(body);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(protocolVersion);
        buffer.writeByte(gameEdition);
        McpeUtil.writeLELengthAsciiString(buffer, chainData);
        McpeUtil.writeLELengthAsciiString(buffer, skinData);
    }
}
