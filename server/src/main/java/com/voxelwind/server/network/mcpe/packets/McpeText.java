package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.api.server.util.TranslatedMessage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeText implements RakNetPackage {
    private TextType type;
    private String source = "";
    private String message = "";
    private TranslatedMessage translatedMessage;

    @Override
    public void decode(ByteBuf buffer) {
        type = TextType.values()[buffer.readByte()];
        switch (type) {
            case RAW:
                message = RakNetUtil.readString(buffer);
                break;
            case SOURCE:
                source = RakNetUtil.readString(buffer);
                message = RakNetUtil.readString(buffer);
                break;
            case TRANSLATE:
                translatedMessage = McpeUtil.readTranslatedMessage(buffer);
                break;
            case POPUP:
                source = RakNetUtil.readString(buffer);
                message = RakNetUtil.readString(buffer);
                break;
            case TIP:
                message = RakNetUtil.readString(buffer);
                break;
            case SYSTEM:
                message = RakNetUtil.readString(buffer);
                break;
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(type.ordinal());
        switch (type) {
            case RAW:
                RakNetUtil.writeString(buffer, message);
                break;
            case SOURCE:
                RakNetUtil.writeString(buffer, source);
                RakNetUtil.writeString(buffer, message);
                break;
            case TRANSLATE:
                McpeUtil.writeTranslatedMessage(buffer, translatedMessage);
                break;
            case POPUP:
                RakNetUtil.writeString(buffer, source);
                RakNetUtil.writeString(buffer, message);
                break;
            case TIP:
                message = RakNetUtil.readString(buffer);
                break;
            case SYSTEM:
                message = RakNetUtil.readString(buffer);
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
