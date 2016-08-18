package com.voxelwind.server.command.builtin;

import com.voxelwind.api.server.MessageRecipient;
import com.voxelwind.api.server.Server;
import com.voxelwind.api.server.command.CommandExecutor;
import com.voxelwind.api.server.command.CommandExecutorSource;

public class VersionCommand implements CommandExecutor {
    private final Server server;

    public VersionCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(CommandExecutorSource source, String[] args) throws Exception {
        if (source instanceof MessageRecipient) {
            ((MessageRecipient) source).sendMessage("This is " + server.getName() + " " + server.getVersion());
        }
    }
}
