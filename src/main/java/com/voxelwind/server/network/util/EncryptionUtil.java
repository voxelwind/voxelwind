package com.voxelwind.server.network.util;

import com.voxelwind.server.network.mcpe.packets.McpeServerHandshake;
import io.netty.buffer.Unpooled;

import javax.crypto.*;
import java.security.*;

public class EncryptionUtil {
    private EncryptionUtil() {

    }

    private static final KeyPair serverKey;
    private static final SecureRandom secureRandom = new SecureRandom();

    static {
        try {
            serverKey = KeyPairGenerator.getInstance("EC").generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static byte[] getSharedSecret(PublicKey clientKey) throws InvalidKeyException {
        KeyAgreement agreement;
        try {
            agreement = KeyAgreement.getInstance("ECDH");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        agreement.init(serverKey.getPrivate());
        agreement.doPhase(clientKey, true);
        return agreement.generateSecret();
    }

    public static KeyPair getServerKey() {
        return serverKey;
    }

    public static McpeServerHandshake createHandshakePacket() {
        McpeServerHandshake handshake = new McpeServerHandshake();
        handshake.setKey(serverKey.getPublic());

        // Generate 16 cryptographically-secure random bytes
        byte[] token = new byte[16];
        secureRandom.nextBytes(token);

        handshake.setToken(Unpooled.wrappedBuffer(token));
        return handshake;
    }
}
