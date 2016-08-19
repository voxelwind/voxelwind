package com.voxelwind.server.event;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.voxelwind.api.server.event.Event;
import com.voxelwind.api.server.event.EventManager;
import com.voxelwind.api.server.event.Listener;
import com.voxelwind.server.event.firehandlers.ReflectionEventFireHandler;

import java.lang.reflect.Method;
import java.util.*;

public class VoxelwindEventManager implements EventManager {
    private volatile Map<Class<? extends Event>, EventFireHandler> eventHandlers = Collections.emptyMap();
    private final List<Object> listeners = new ArrayList<>();
    private final Map<Object, List<Object>> listenersByPlugin = new HashMap<>();
    private final Object registerLock = new Object();

    @Override
    public void register(Object plugin, Object listener) {
        Preconditions.checkNotNull(plugin, "plugin");
        Preconditions.checkNotNull(listener, "listener");

        // Verify that all listeners are valid.
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Listener.class)) {
                if (method.getParameterCount() != 1) {
                    throw new IllegalArgumentException("Method " + method.getName() + " in " + listener + " has more than one parameter.");
                }

                if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    throw new IllegalArgumentException("Method " + method.getName() + " in " + listener + " does not accept a subclass of Event.");
                }

                method.setAccessible(true);
            }
        }

        synchronized (registerLock) {
            listenersByPlugin.computeIfAbsent(plugin, k -> new ArrayList<>()).add(listener);
            listeners.add(listener);
            bakeHandlers();
        }
    }

    @Override
    public void fire(Event event) {
        Preconditions.checkNotNull(event, "event");
        EventFireHandler handler = eventHandlers.get(event.getClass());
        if (handler != null) {
            handler.fire(event);
        }
    }

    @Override
    public void unregisterListener(Object listener) {
        Preconditions.checkNotNull(listener, "listener");
        synchronized (registerLock) {
            for (List<Object> objects : listenersByPlugin.values()) {
                objects.remove(listener);
            }
            listeners.remove(listener);
            bakeHandlers();
        }
    }

    @Override
    public void unregisterAllListeners(Object plugin) {
        Preconditions.checkNotNull(plugin, "plugin");
        synchronized (registerLock) {
            List<Object> objects = listenersByPlugin.remove(plugin);
            if (objects != null) {
                listeners.removeAll(objects);
                bakeHandlers();
            }
        }
    }

    private void bakeHandlers() {
        Map<Class<? extends Event>, List<ReflectionEventFireHandler.ListenerMethod>> listenerMap = new HashMap<>();

        for (Object listener : listeners) {
            for (Method method : listener.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Listener.class)) {
                    listenerMap.computeIfAbsent((Class<? extends Event>) method.getParameterTypes()[0], (k) -> new ArrayList<>())
                            .add(new ReflectionEventFireHandler.ListenerMethod(listener, method));
                }
            }
        }

        for (List<ReflectionEventFireHandler.ListenerMethod> methods : listenerMap.values()) {
            Collections.sort(methods);
        }

        Map<Class<? extends Event>, EventFireHandler> handlerMap = new HashMap<>();
        for (Map.Entry<Class<? extends Event>, List<ReflectionEventFireHandler.ListenerMethod>> entry : listenerMap.entrySet()) {
            handlerMap.put(entry.getKey(), new ReflectionEventFireHandler(entry.getValue()));
        }
        this.eventHandlers = ImmutableMap.copyOf(handlerMap);
    }
}
