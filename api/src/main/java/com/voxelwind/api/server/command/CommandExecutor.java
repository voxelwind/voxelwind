package com.voxelwind.api.server.command;

/**
 * A class that implements a command. The command may be executed asynchronously and concurrently.
 */
public interface CommandExecutor {
    /**
     * Executes the command for a specified {@code source}.
     * @param source the source of the command
     * @param args the arguments for the command
     */
    void execute(CommandExecutorSource source, String[] args) throws Exception;
}
