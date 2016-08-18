package com.voxelwind.server.event;

import com.google.common.base.Preconditions;
import com.voxelwind.api.server.event.Event;
import com.voxelwind.api.server.event.EventManager;
import com.voxelwind.api.server.event.Listener;
import com.voxelwind.server.event.firehandlers.ReflectionEventFireHandler;

import java.lang.reflect.Method;
import java.util.*;

public class VoxelwindEventManager implements EventManager {
    private final Map<Class<? extends Event>, EventFireHandler> eventHandlers = new HashMap<>();
    private final List<Object> listeners = new ArrayList<>();
    private final Map<Object, List<Object>> listenersByPlugin = new HashMap<>();

    @Override
    public void register(Object plugin, Object listener) {
        Preconditions.checkNotNull(plugin, "plugin");
        Preconditions.checkNotNull(listener, "listener");
        listenersByPlugin.computeIfAbsent(plugin, k -> new ArrayList<>()).add(listener);
        listeners.add(listener);
        bakeHandlers();
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
        for (List<Object> objects : listenersByPlugin.values()) {
            objects.remove(listener);
        }
        listeners.remove(listener);
        bakeHandlers();
    }

    @Override
    public void unregisterAllListeners(Object plugin) {
        Preconditions.checkNotNull(plugin, "plugin");
        List<Object> objects = listenersByPlugin.remove(plugin);
        if (objects != null) {
            listeners.removeAll(objects);
            bakeHandlers();
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

        for (Map.Entry<Class<? extends Event>, List<ReflectionEventFireHandler.ListenerMethod>> entry : listenerMap.entrySet()) {
            eventHandlers.put(entry.getKey(), new ReflectionEventFireHandler(entry.getValue()));
        }
    }
}
