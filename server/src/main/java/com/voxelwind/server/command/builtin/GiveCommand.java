package com.voxelwind.server.command.builtin;

import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.util.TextFormat;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.command.CommandExecutor;
import com.voxelwind.api.server.command.CommandExecutorSource;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import com.voxelwind.server.game.serializer.MetadataSerializer;

public class GiveCommand implements CommandExecutor {
    @Override
    public void execute(CommandExecutorSource source, String[] args) throws Exception {
        if (source instanceof Player) {
            if (args.length == 0) {
                ((Player) source).sendMessage(TextFormat.RED + "/give <item ID> <amount> <data>");
                return;
            }

            int id = Integer.parseInt(args[0]);
            int amount = args.length >= 2 ? Integer.parseInt(args[1]) : 1;
            ItemType type = ItemTypes.forId(id);
            Metadata metadata = args.length >= 3 ? MetadataSerializer.deserializeMetadata(type, Short.parseShort(args[2])) : null;
            ((Player) source).getInventory().addItem(new VoxelwindItemStack(type, amount, metadata, null));
        }
    }
}
