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
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", "BC");
            generator.initialize(256);
            serverKey = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static byte[] getServerKey(PublicKey key, byte[] token) throws InvalidKeyException {
        byte[] sharedSecret = getSharedSecret(key);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        digest.update(token);
        digest.update(sharedSecret);
        return digest.digest();
    }

    private static byte[] getSharedSecret(PublicKey clientKey) throws InvalidKeyException {
        KeyAgreement agreement;
        try {
            agreement = KeyAgreement.getInstance("ECDH", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new AssertionError(e);
        }

        agreement.init(serverKey.getPrivate());
        agreement.doPhase(clientKey, true);
        return agreement.generateSecret();
    }

    public static KeyPair getServerKey() {
        return serverKey;
    }

    public static McpeServerHandshake createHandshakePacket(byte[] token) {
        McpeServerHandshake handshake = new McpeServerHandshake();
        handshake.setKey(serverKey.getPublic());
        handshake.setToken(Unpooled.wrappedBuffer(token));
        return handshake;
    }

    public static byte[] generateRandomToken() {
        byte[] token = new byte[16];
        secureRandom.nextBytes(token);
        return token;
    }
}
