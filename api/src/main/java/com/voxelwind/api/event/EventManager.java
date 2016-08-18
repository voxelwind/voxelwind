package com.voxelwind.api.event;

/**
 * This class manages event listeners and fires events.
 */
public interface EventManager {
    /**
     * Registers an object with event listeners.
     * @param listener the object
     */
    void register(Object listener);

    /**
     * Fires an event.
     * @param event the event to fire
     */
    void fire(Event event);

    /**
     * Unregisters an object's event listeners.
     * @param listener the object to deregister
     */
    void unregister(Object listener);
}
