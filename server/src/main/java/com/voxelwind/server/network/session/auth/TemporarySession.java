package com.voxelwind.server.network.session.auth;

import com.voxelwind.api.server.Session;
import com.voxelwind.server.network.session.McpeSession;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;

public class TemporarySession implements Session {
    private final McpeSession session;

    public TemporarySession(McpeSession session) {
        this.session = session;
    }

    @Nonnull
    @Override
    public String getName() {
        return session.getAuthenticationProfile().getDisplayName();
    }

    @Nonnull
    @Override
    public Optional<InetSocketAddress> getRemoteAddress() {
        return session.getRemoteAddress();
    }

    @Nonnull
    @Override
    public UUID getUniqueId() {
        return session.getAuthenticationProfile().getIdentity();
    }

    @Override
    public boolean isXboxAuthenticated() {
        return session.getAuthenticationProfile().getXuid() != null;
    }

    @Nonnull
    @Override
    public OptionalLong getXuid() {
        return session.getAuthenticationProfile().getXuid() == null ? OptionalLong.empty() :
                OptionalLong.of(session.getAuthenticationProfile().getXuid());
    }
}
