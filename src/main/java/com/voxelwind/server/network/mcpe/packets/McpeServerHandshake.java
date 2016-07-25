package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class McpeServerHandshake implements RakNetPackage {
    private PublicKey key;
    private ByteBuf token;

    @Override
    public void decode(ByteBuf buffer) {
        String keyBase64 = RakNetUtil.readString(buffer);
        byte[] keyArray = Base64.getDecoder().decode(keyBase64);
        try {
            key = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(keyArray));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
        short tokenSz = buffer.readShort();
        token = buffer.readBytes(tokenSz);
    }

    @Override
    public void encode(ByteBuf buffer) {
        byte[] encoded = key.getEncoded();
        RakNetUtil.writeString(buffer, Base64.getEncoder().encodeToString(encoded));
        buffer.writeShort(buffer.readableBytes());
        buffer.writeBytes(token);
    }

    public PublicKey getKey() {
        return key;
    }

    public void setKey(PublicKey key) {
        this.key = key;
    }

    public ByteBuf getToken() {
        return token;
    }

    public void setToken(ByteBuf token) {
        this.token = token;
    }
}
