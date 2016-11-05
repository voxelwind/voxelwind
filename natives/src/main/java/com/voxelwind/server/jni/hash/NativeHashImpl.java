package com.voxelwind.server.jni.hash;

public class NativeHashImpl
{

    native long init();

    native void update(long ctx, long in, int length);

    native byte[] digest(long ctx);

    native void free(long ctx);
}
