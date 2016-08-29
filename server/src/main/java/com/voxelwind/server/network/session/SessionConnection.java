package com.voxelwind.server.network.session;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * Handles a session connection.
 */
public interface SessionConnection {
    /**
     * Returns the remote address for this connection, if any.
     * @return the remote address
     */
    Optional<InetSocketAddress> getRemoteAddress();

    /**
     * Closes this connection. No further packets can be received or sent after this function is called.
     */
    void close();

    /**
     * Sends data to the remote connection.
     * @param data the data to send
     */
    void sendPacket(@Nonnull ByteBuf data);

    /**
     * Returns whether or not the session has been closed.
     * @return whether or not the session has been closed
     */
    boolean isClosed();

    /**
     * Called every 50 milliseconds from the session object.
     */
    void onTick();
}
