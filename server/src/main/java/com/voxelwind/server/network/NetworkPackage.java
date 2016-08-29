package com.voxelwind.server.network;

import io.netty.buffer.ByteBuf;

public interface NetworkPackage {
    /**
     * Decodes a packet of the specified buffer. The buffer is always assumed to have the proper position and length
     * to complete the read.
     *
     * @param buffer the buffer to read of
     */
    void decode(ByteBuf buffer);

    /**
     * Encodes a packet to the specified buffer.
     *
     * @param buffer the buffer to write to
     */
    void encode(ByteBuf buffer);
}
