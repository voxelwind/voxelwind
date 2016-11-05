package com.voxelwind.server.jni.hash;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.jni.NativeCode;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

public class NativeHashTest
{

    private static final byte[] INPUT_1 = "Hello, world".getBytes();
    private static final byte[] INPUT_2 = "Voxelwind".getBytes();
    private static final byte[] EXPECTED_HASH_1 = DatatypeConverter.parseHexBinary("4ae7c3b6ac0beff671efa8cf57386151c06e58ca53a78d83f36107316cec125f");
    private static final byte[] EXPECTED_HASH_2 = DatatypeConverter.parseHexBinary("212521264f1636c6765e39e05541972c402c5e0a5a922024fed83adddcf1d51d");

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

        ByteBuf buf1 = Unpooled.directBuffer();
        buf1.writeBytes(INPUT_1);
        hash.update( buf1 );
        byte[] out = hash.digest();

        Assert.assertArrayEquals( "First hash does not match", EXPECTED_HASH_1, out );

        // Test multiple hashes with same instance
        ByteBuf buf2 = Unpooled.directBuffer();
        buf2.writeBytes(INPUT_2);
        hash.update( buf2 );
        byte[] out2 = hash.digest();

        Assert.assertArrayEquals( "Second hash does not match", EXPECTED_HASH_2, out2 );

        buf1.release();
        buf2.release();
    }
}
