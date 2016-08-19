package com.voxelwind.server.event;

import com.voxelwind.api.server.event.Event;
import com.voxelwind.api.server.event.Listener;
import com.voxelwind.api.server.event.server.ServerStartEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        TestEvent2 unregistered = new TestEvent2(1);
        manager.fire(unregistered);
        unregistered.validate();
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

    @Test
    public void fireVoxelwindEvent() throws Exception {
        VoxelwindEventManager manager = new VoxelwindEventManager();
        TestListener2 listener = new TestListener2();
        manager.register(new Object(), listener);
        manager.fire(ServerStartEvent.INSTANCE);
        assertTrue("Event did not successfully fire", listener.isFired());
    }

    public static class TestEvent implements Event {
        protected int currentCount;
        protected final int originalCount;

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

    public static class TestEvent2 extends TestEvent {
        private TestEvent2(int currentCount) {
            super(currentCount);
        }

        public void validate() {
            assertEquals("Event was exhausted", originalCount, currentCount);
        }
    }

    public static class TestListener {
        @Listener
        public void test(TestEvent event) {
            event.decrement();
        }
    }

    public static class TestListener2 {
        private boolean fired = false;

        @Listener
        public void test(ServerStartEvent event) {
            fired = true;
        }

        public boolean isFired() {
            return fired;
        }
    }
}