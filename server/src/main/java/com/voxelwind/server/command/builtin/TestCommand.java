package com.voxelwind.server.command.builtin;

import com.voxelwind.api.game.entities.monsters.Zombie;
import com.voxelwind.api.game.level.Level;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.command.CommandExecutor;
import com.voxelwind.api.server.command.CommandExecutorSource;

/**
 * Created by andrew on 10/24/16.
 */
public class TestCommand implements CommandExecutor {
    @Override
    public void execute(CommandExecutorSource source, String[] args) throws Exception {
        if (source instanceof Player) {
            Player player = (Player) source;
            Level level = player.getLevel();
            double xDiff = 0;
            level.spawn(Zombie.class, level.getSpawnLocation().add(xDiff += 1.5, 0, 0));
        }
    }
}
