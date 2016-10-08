package com.voxelwind.server.jni.hash;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.jni.NativeCode;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

public class NativeHashTest
{

    private static final byte[] INPUT = "Hello, world".getBytes();
    private static final byte[] EXPECTED_HASH = DatatypeConverter.parseHexBinary("4ae7c3b6ac0beff671efa8cf57386151c06e58ca53a78d83f36107316cec125f");

    private final NativeCode<VoxelwindHash> factory = new NativeCode( "native-hash", JavaHash.class, NativeHash.class );

    @Test
    public void doTest()
    {
        if ( NativeCode.isSupported() )
        {
            Assert.assertTrue( "Native code failed to load!", factory.load() );
            test( factory.newInstance() );
        }
        test( new JavaHash() );
    }

    private void test(VoxelwindHash hash)
    {
        System.out.println( "Testing: " + hash );

        ByteBuf buf = Unpooled.directBuffer();
        buf.writeBytes( INPUT );

        hash.update( buf );
        byte[] out = hash.digest();

        Assert.assertArrayEquals( "Hashes do not match", EXPECTED_HASH, out );

        buf.release();
    }
}
