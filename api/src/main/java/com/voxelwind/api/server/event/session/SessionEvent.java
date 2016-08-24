package com.voxelwind.api.server.event.session;

import com.voxelwind.api.server.Session;
import com.voxelwind.api.server.event.Event;

/**
 * Denotes an event dealing with sessions.
 */
public interface SessionEvent extends Event {
    /**
     * Returns the relevant session in question.
     * @return the session
     */
    Session getSession();
}
