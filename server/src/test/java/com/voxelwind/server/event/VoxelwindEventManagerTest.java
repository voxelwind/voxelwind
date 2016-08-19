package com.voxelwind.server.event;

import com.voxelwind.api.server.event.Event;
import com.voxelwind.api.server.event.Listener;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VoxelwindEventManagerTest {
    @Test
    public void fire() throws Exception {
        VoxelwindEventManager manager = new VoxelwindEventManager();

        TestEvent event = new TestEvent(0);
        manager.fire(event);
        event.validate();

        manager.register(new Object(), new TestListener());
        event = new TestEvent(1);
        manager.fire(event);
        event.validate();
    }

    @Test
    public void unregisterListener() throws Exception {
        VoxelwindEventManager manager = new VoxelwindEventManager();
        TestListener listener = new TestListener();
        manager.register(new Object(), listener);
        TestEvent event = new TestEvent(1);
        manager.fire(event);
        event.validate();

        manager.unregisterListener(listener);

        event = new TestEvent(0);
        manager.fire(event);
        event.validate();
    }

    public static class TestEvent implements Event {
        private int currentCount;
        private final int originalCount;

        private TestEvent(int currentCount) {
            this.currentCount = currentCount;
            this.originalCount = currentCount;
        }

        public void decrement() {
            currentCount--;
        }

        public void validate() {
            assertEquals("Event was not exhausted", 0, currentCount);
        }
    }

    public static class TestListener {
        @Listener
        public void test(TestEvent event) {
            event.decrement();
        }
    }
}