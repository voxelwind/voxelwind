package com.voxelwind.api.server.event.server;

import com.voxelwind.api.server.event.Event;

/**
 * This event will be fired when the server has disconnected clients, stopped accepting new connections and after levels
 * have been deinitialized but before the server process exits. At this point, you should perform any last-minute clean
 * up before the process exits.
 */
public interface ServerStopEvent extends Event {
    static ServerStopEvent create() {
        return new ServerStopEvent() {};
    }
}
