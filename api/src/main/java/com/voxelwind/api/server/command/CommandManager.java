package com.voxelwind.api.server.command;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface CommandManager {
    void register(String command, CommandExecutor executor);

    void executeCommand(CommandExecutorSource source, String command) throws CommandException;

    void unregister(String command);
}
