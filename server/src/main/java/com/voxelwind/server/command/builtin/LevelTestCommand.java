package com.voxelwind.server.command.builtin;

import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.command.CommandExecutor;
import com.voxelwind.api.server.command.CommandExecutorSource;
import com.voxelwind.server.VoxelwindServer;

/**
 * Created by andrew on 10/6/16.
 */
public class LevelTestCommand implements CommandExecutor {
    @Override
    public void execute(CommandExecutorSource source, String[] args) throws Exception {
        if (source instanceof Player) {
            VoxelwindServer server = (VoxelwindServer) ((Player) source).getServer();
            server.getDefaultLevel2().getBlock(server.getDefaultLevel2().getSpawnLocation().toInt()).whenComplete((x, t) -> {
                if (t != null) return;
                ((Player) source).teleport(server.getDefaultLevel2(), server.getDefaultLevel2().getSpawnLocation());
            });
        }
    }
}
