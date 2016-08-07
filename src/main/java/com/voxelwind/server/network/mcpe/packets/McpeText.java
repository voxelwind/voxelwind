package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;

public class McpeText implements RakNetPackage {
    private TextType type;
    private String source = "";
    private String message = "";

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
                throw new UnsupportedOperationException("Translate packets currently unsupported");
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
                throw new UnsupportedOperationException("Translate packets currently unsupported");
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
                '}';
    }

    public TextType getType() {
        return type;
    }

    public void setType(TextType type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
