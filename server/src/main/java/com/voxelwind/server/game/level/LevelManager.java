package com.voxelwind.server.game.level;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.level.Level;

import java.util.*;

public class LevelManager {
    private final List<Level> levels = new ArrayList<>();
    private final Map<Level, LevelTicker> levelTasks = new HashMap<>();

    public synchronized void register(Level level) {
        Preconditions.checkNotNull(level, "level");
        Preconditions.checkArgument(!levels.contains(level), "level already registered");
        levels.add(level);
    }

    public synchronized void start(VoxelwindLevel level) {
        Preconditions.checkNotNull(level, "level");
        Preconditions.checkState(!levelTasks.containsKey(level), "level already being ticked");
        LevelTicker ticker = new LevelTicker(level);
        levelTasks.put(level, ticker);
    }

    public synchronized void stop(Level level) {
        Preconditions.checkNotNull(level, "level");
        LevelTicker ticker = levelTasks.remove(level);
        Preconditions.checkState(ticker != null, "level is not being ticked");
        ticker.stop();
    }

    private class LevelTicker extends TimerTask {
        private final Timer timer;
        private final VoxelwindLevel level;

        private LevelTicker(VoxelwindLevel level) {
            this.level = level;
            this.timer = new Timer("Voxelwind level ticker - " + level.getName(), true);
            this.timer.scheduleAtFixedRate(this, 50, 50);
        }

        public void stop() {
            timer.cancel();
        }

        @Override
        public void run() {
            level.onTick();
        }
    }
}
