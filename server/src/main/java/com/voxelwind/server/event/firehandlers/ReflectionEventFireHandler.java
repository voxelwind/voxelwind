package com.voxelwind.server.event.firehandlers;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.server.event.Event;
import com.voxelwind.api.server.event.Listener;
import com.voxelwind.server.event.EventFireHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * A {@link com.voxelwind.server.event.EventFireHandler} that uses reflection to call the method.
 */
public class ReflectionEventFireHandler implements EventFireHandler {
    private static final Logger LOGGER = LogManager.getLogger(ReflectionEventFireHandler.class);
    private final List<ListenerMethod> methods;

    public ReflectionEventFireHandler(Collection<ListenerMethod> methods) {
        this.methods = ImmutableList.copyOf(methods);
    }

    @Override
    public void fire(Event event) {
        for (int i = 0; i < methods.size(); i++) {
            try {
                LOGGER.info("Calling {}", methods.get(i));
                methods.get(i).run(event);
            } catch (InvocationTargetException | IllegalAccessException e) {
                LOGGER.error("Exception occurred while executing method " + methods.get(i) + " for " + event, e);
            }
        }
    }

    public static class ListenerMethod implements Comparable<ListenerMethod> {
        private final Object listener;
        private final Method method;

        public ListenerMethod(Object listener, Method method) {
            this.listener = listener;
            this.method = method;
        }

        public void run(Event event) throws InvocationTargetException, IllegalAccessException {
            method.invoke(listener, event);
        }

        @Override
        public String toString() {
            return listener.getClass().getName() + "#" + method.getName();
        }

        public Object getListener() {
            return listener;
        }

        public Method getMethod() {
            return method;
        }

        @Override
        public int compareTo(@Nonnull ListenerMethod o) {
            Listener listener = getMethod().getAnnotation(Listener.class);
            if (listener == null) {
                return -1;
            }

            Listener listener2 = o.getMethod().getAnnotation(Listener.class);
            if (listener2 == null) {
                return 1;
            }

            return Integer.compare(listener.order(), listener2.order());
        }
    }
}
