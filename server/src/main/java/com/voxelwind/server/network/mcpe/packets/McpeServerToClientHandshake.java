package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.annotations.BatchDisallowed;
import com.voxelwind.server.network.mcpe.annotations.ForceClearText;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@ForceClearText
@BatchDisallowed
@Data
public class McpeServerToClientHandshake implements NetworkPackage {
    private PublicKey key;
    private byte[] token;

    @Override
    public void decode(ByteBuf buffer) {
        String keyBase64 = McpeUtil.readVarintLengthString(buffer);
        byte[] keyArray = Base64.getDecoder().decode(keyBase64);
        try {
            key = KeyFactory.getInstance("EC", "BC").generatePublic(new X509EncodedKeySpec(keyArray));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new AssertionError(e);
        }
        int tokenSz = (int) Varints.decodeUnsigned(buffer);
        token = new byte[tokenSz];
        buffer.readBytes(token);
    }

    @Override
    public void encode(ByteBuf buffer) {
        byte[] encoded = key.getEncoded();
        McpeUtil.writeVarintLengthString(buffer, Base64.getEncoder().encodeToString(encoded));
        Varints.encodeUnsigned(buffer, token.length);
        buffer.writeBytes(token);
    }
}
