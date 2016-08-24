package com.voxelwind.api.server.event.session;

import com.google.common.base.Preconditions;
import com.voxelwind.api.server.Session;

import javax.annotation.Nonnull;

/**
 * This event is called after the session has been authenticated but before a player session has been set up.
 */
public class SessionLoginEvent implements SessionEvent {
    private final Session session;
    private String disconnectReason;

    public SessionLoginEvent(Session session) {
        this.session = Preconditions.checkNotNull(session, "session");
    }

    @Nonnull
    @Override
    public Session getSession() {
        return session;
    }

    /**
     * Determines if a plugin has already decided to disconnect the session.
     * @return if the session will be disconnected
     */
    public boolean willDisconnect() {
        return disconnectReason != null;
    }

    /**
     * Returns the reason for disconnecting the session. If this is {@code null}, no disconnect will be performed.
     * @return the disconnection reason
     */
    public String getDisconnectReason() {
        return disconnectReason;
    }

    /**
     * Sets the reason for disconnecting the session. If this is set to {@code null}, no disconnect will be performed.
     * @param disconnectReason the reason
     */
    public void setDisconnectReason(String disconnectReason) {
        this.disconnectReason = disconnectReason;
    }
}
