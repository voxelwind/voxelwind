package com.voxelwind.server.jni.hash;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;

public class NativeHash implements VoxelwindHash
{

    private static final NativeHashImpl impl = new NativeHashImpl();
    private final long ctx;
    private boolean completed = false;

    public NativeHash()
    {
        ctx = impl.init();
    }

    @Override
    public void update(ByteBuf buf)
    {
        // Smoke tests
        checkState();
        buf.memoryAddress();

        // Update the digest
        impl.update( ctx, buf.memoryAddress() + buf.readerIndex(), buf.readableBytes() );

        // Go to the end of the buffer, all bytes would of been read
        buf.readerIndex( buf.writerIndex() );
    }

    @Override
    public byte[] digest()
    {
        checkState();
        byte[] digest = impl.digest(ctx);
        completed = true;
        return digest;
    }

    private void checkState()
    {
        Preconditions.checkState( !completed, "Already used" );
    }
}
