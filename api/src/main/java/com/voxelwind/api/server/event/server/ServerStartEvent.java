package com.voxelwind.api.server.event.server;

import com.voxelwind.api.server.event.Event;

/**
 * This event is fired immediately after the server has been completely initialized and is accepting connections.
 */
public class ServerStartEvent implements Event {
    public static final ServerStartEvent INSTANCE = new ServerStartEvent();

    private ServerStartEvent() {

    }
}
