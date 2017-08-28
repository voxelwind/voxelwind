package com.voxelwind.server.jni;

import lombok.experimental.UtilityClass;

import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public class CryptoUtil
{
    public static boolean isJCEUnlimitedStrength()
    {
        try
        {
            return Cipher.getMaxAllowedKeyLength( "AES" ) == Integer.MAX_VALUE;
        } catch ( NoSuchAlgorithmException e )
        {
            // AES should always exist.
            throw new AssertionError( e );
        }
    }
}
