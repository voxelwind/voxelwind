package com.voxelwind.server.command.builtin;

import com.voxelwind.api.game.level.block.types.BlockTypes;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.command.CommandExecutor;
import com.voxelwind.api.server.command.CommandExecutorSource;
import com.voxelwind.server.game.item.VoxelwindItemStack;

public class GiveItemTestCommand implements CommandExecutor {
    @Override
    public void execute(CommandExecutorSource source, String[] args) throws Exception {
        if (source instanceof Player) {
            ((Player) source).getInventory().addItem(new VoxelwindItemStack(BlockTypes.DIRT, 1, null));
        }
    }
}
