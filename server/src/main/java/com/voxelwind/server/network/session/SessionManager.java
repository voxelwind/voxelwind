package com.voxelwind.server.network.session;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.voxelwind.api.server.Player;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SessionManager {
    // Voxelwind ticks each session in a thread pool. This thread pool is automatically adjusted depending on player count.
    private static final int SESSIONS_PER_THREAD = 50;

    private final ConcurrentMap<InetSocketAddress, McpeSession> sessions = new ConcurrentHashMap<>();
    private final ThreadPoolExecutor sessionTicker = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("Voxelwind Session Ticker - #%d").setDaemon(true).build());

    public boolean add(InetSocketAddress address, McpeSession session) {
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

    public McpeSession get(InetSocketAddress address) {
        return sessions.get(address);
    }

    public Collection<McpeSession> all() {
        return ImmutableList.copyOf(sessions.values());
    }

    public long countConnected() {
        return sessions.values().stream()
                .filter(p -> p.getState() == SessionState.CONNECTED)
                .count();
    }

    public List<Player> allPlayers() {
        return sessions.values().stream()
                .filter(p -> p.getPlayerSession() != null)
                .map(McpeSession::getPlayerSession)
                .collect(Collectors.toList());
    }

    private void adjustPoolSize() {
        int threads = Math.max(1, sessions.size() / SESSIONS_PER_THREAD);
        if (sessionTicker.getMaximumPoolSize() != threads) {
            sessionTicker.setMaximumPoolSize(threads);
        }
    }

    public void onTick() {
        for (McpeSession session : sessions.values()) {
            sessionTicker.execute(session::onTick);
        }
    }
}
