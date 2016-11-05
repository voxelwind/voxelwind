package com.voxelwind.server.jni.hash;

import io.netty.buffer.ByteBuf;

public interface VoxelwindHash
{

    void update(ByteBuf buf);

    byte[] digest();

    void free();
}
