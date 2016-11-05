package com.voxelwind.server.jni.hash;

import io.netty.buffer.ByteBuf;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JavaHash implements VoxelwindHash
{

    private final MessageDigest digest;

    public JavaHash()
    {
        try
        {
            digest = MessageDigest.getInstance("SHA-256");
        } catch ( NoSuchAlgorithmException e )
        {
            // Can't possibly happen as SHA-256 is required by the MessageDigest class to be present.
            throw new AssertionError( e );
        }
    }

    @Override
    public void update(ByteBuf buf)
    {
        byte[] bytes = new byte[ buf.readableBytes() ];
        buf.readBytes( bytes );
        digest.update( bytes );
    }

    @Override
    public byte[] digest()
    {
        return digest.digest();
    }

    @Override
    public void free()
    {
        // No-op.
    }
}
