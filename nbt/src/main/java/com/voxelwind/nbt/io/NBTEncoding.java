package com.voxelwind.nbt.io;

/**
 * Represents the type of NBT represented by this stream.
 */
public enum NBTEncoding {
    /**
     * Plain, uncompressed Notchian encoding.
     */
    NOTCHIAN,
    /**
     * A special NBT encoding introduced in MCPE 0.16.
     */
    MCPE_0_16_NETWORK
}
