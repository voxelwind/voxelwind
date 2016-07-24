package io.minimum.voxelwind.network.util;

import javax.crypto.*;
import java.security.*;

public class EncryptionUtil {
    private EncryptionUtil() {

    }

    private static final KeyPair serverKey;

    static {
        try {
            serverKey = KeyPairGenerator.getInstance("EC").generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static byte[] getSharedSecret(PublicKey clientKey) {
        KeyAgreement agreement;
        try {
            agreement = KeyAgreement.getInstance("ECDH");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        try {
            agreement.init(serverKey.getPrivate());
            agreement.doPhase(clientKey, true);
        } catch (InvalidKeyException e) {
            throw new AssertionError(e);
        }

        return agreement.generateSecret();
    }

    public static KeyPair getServerKey() {
        return serverKey;
    }
}
