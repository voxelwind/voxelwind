package com.voxelwind.server.event;

import com.voxelwind.api.event.Event;

public interface EventFireHandler {
    void fire(Event event);
}
