package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.api.server.player.TranslatedMessage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeText implements NetworkPackage {
    private TextType type;
    private String source = "";
    private String message = "";
    private TranslatedMessage translatedMessage;

    @Override
    public void decode(ByteBuf buffer) {
        type = TextType.values()[buffer.readByte()];
        switch (type) {
            case SOURCE:
            case POPUP:
                source = McpeUtil.readVarintLengthString(buffer);
                // Intentional fall-through.
            case RAW:
            case TIP:
            case SYSTEM:
                message = McpeUtil.readVarintLengthString(buffer);
                break;
            case TRANSLATE:
                translatedMessage = McpeUtil.readTranslatedMessage(buffer);
                break;
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(type.ordinal());
        switch (type) {
            case SOURCE:
            case POPUP:
                McpeUtil.writeVarintLengthString(buffer, source);
                // Intentional fall-through.
            case RAW:
            case TIP:
            case SYSTEM:
                McpeUtil.writeVarintLengthString(buffer, message);
                break;
            case TRANSLATE:
                McpeUtil.writeTranslatedMessage(buffer, translatedMessage);
                break;
        }
    }

    @Override
    public String toString() {
        return "McpeText{" +
                "type=" + type +
                ", source='" + source + '\'' +
                ", message='" + message + '\'' +
                ", translatedMessage=" + translatedMessage +
                '}';
    }

    public enum TextType {
        RAW,
        SOURCE,
        TRANSLATE,
        POPUP,
        TIP,
        SYSTEM
    }
}
