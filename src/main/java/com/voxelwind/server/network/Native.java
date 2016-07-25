package com.voxelwind.server.network;

import net.md_5.bungee.jni.NativeCode;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.jni.cipher.JavaCipher;
import net.md_5.bungee.jni.cipher.NativeCipher;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import net.md_5.bungee.jni.zlib.JavaZlib;
import net.md_5.bungee.jni.zlib.NativeZlib;

public class Native {
    private Native() {

    }

    public static final NativeCode<BungeeZlib> zlib = new NativeCode("native-compress", JavaZlib.class, NativeZlib.class);

    public static final NativeCode<BungeeCipher> cipher = new NativeCode("native-cipher", JavaCipher.class, NativeCipher.class);
}
