package io.minimum.voxelwind.network.util;

import io.netty.buffer.ByteBuf;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class EncryptionUtil {
    private EncryptionUtil() {

    }

    private static final SecretKey serverKey;
    private static final ThreadLocal<byte[]> heapInLocal = new EmptyByteThreadLocal();
    private static final ThreadLocal<byte[]> heapOutLocal = new EmptyByteThreadLocal();

    private static class EmptyByteThreadLocal extends ThreadLocal<byte[]> {
        @Override
        protected byte[] initialValue() {
            return new byte[0];
        }
    }


    static {
        try {
            serverKey = KeyGenerator.getInstance("EC").generateKey();
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
            agreement.init(serverKey);
            agreement.doPhase(clientKey, true);
        } catch (InvalidKeyException e) {
            throw new AssertionError(e);
        }

        return agreement.generateSecret();
    }

    public static SecretKey getServerKey() {
        return serverKey;
    }

    public static void aesEncrypt(ByteBuf in, ByteBuf out, Cipher cipher) throws ShortBufferException {
        int readableBytes = in.readableBytes();
        byte[] heapIn = bufToByte( in );

        byte[] heapOut = heapOutLocal.get();
        int outputSize = cipher.getOutputSize( readableBytes );
        if ( heapOut.length < outputSize )
        {
            heapOut = new byte[ outputSize ];
            heapOutLocal.set( heapOut );
        }
        out.writeBytes( heapOut, 0, cipher.update( heapIn, 0, readableBytes, heapOut ) );
    }

    private static byte[] bufToByte(ByteBuf in) {
        byte[] heapIn = heapInLocal.get();
        int readableBytes = in.readableBytes();
        if (heapIn.length < readableBytes)
        {
            heapIn = new byte[readableBytes];
            heapInLocal.set( heapIn );
        }
        in.readBytes(heapIn, 0, readableBytes);
        return heapIn;
    }
}
