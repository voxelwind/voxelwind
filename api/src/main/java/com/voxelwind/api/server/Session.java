package com.voxelwind.api.server;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.OptionalLong;
import java.util.UUID;

/**
 * This interface represents a connection is that in progress.
 */
public interface Session {
    @Nonnull
    String getName();

    @Nonnull
    InetSocketAddress getRemoteAddress();

    @Nonnull
    UUID getUniqueId();

    boolean isXboxAuthenticated();

    @Nonnull
    OptionalLong getXuid();
}
