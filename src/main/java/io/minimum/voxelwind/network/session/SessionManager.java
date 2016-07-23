package io.minimum.voxelwind.network.session;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SessionManager {
    // Voxelwind ticks each session in a thread pool. This thread pool is automatically adjusted depending on player count.
    private static final int SESSIONS_PER_THREAD = 50;

    private final ConcurrentMap<InetSocketAddress, UserSession> sessions = new ConcurrentHashMap<>();
    private final ThreadPoolExecutor sessionTicker = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("Voxelwind Session Ticker - #%d").setDaemon(true).build());

    public boolean add(InetSocketAddress address, UserSession session) {
        boolean added = sessions.putIfAbsent(address, session) == null;
        if (added) {
            adjustPoolSize();
        }
        return added;
    }

    public boolean remove(InetSocketAddress address) {
        boolean removed = sessions.remove(address) != null;
        if (removed) {
            adjustPoolSize();
        }
        return removed;
    }

    public UserSession get(InetSocketAddress address) {
        return sessions.get(address);
    }

    public Collection<UserSession> all() {
        return ImmutableList.copyOf(sessions.values());
    }

    public long countConnected() {
        return sessions.values().stream()
                .filter(p -> p.getState() == SessionState.CONNECTED)
                .count();
    }

    public List<UserSession> allConnected() {
        return sessions.values().stream()
                .filter(p -> p.getState() == SessionState.CONNECTED)
                .collect(Collectors.toList());
    }

    private void adjustPoolSize() {
        int threads = sessions.size() / SESSIONS_PER_THREAD;
        sessionTicker.setMaximumPoolSize(threads);
    }

    public void onTick() {
        for (UserSession session : sessions.values()) {
            sessionTicker.execute(session::onTick);
        }
    }
}
