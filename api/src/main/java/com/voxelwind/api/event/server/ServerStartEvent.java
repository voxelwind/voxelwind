package com.voxelwind.api.event.server;

import com.voxelwind.api.event.Event;

/**
 * This event is fired immediately after the server has been completely initialized and is accepting connections.
 */
public interface ServerStartEvent extends Event {
    static ServerStartEvent create() {
        return new ServerStartEvent() {};
    }
}
