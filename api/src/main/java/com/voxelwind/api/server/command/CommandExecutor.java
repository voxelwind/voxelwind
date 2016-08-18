package com.voxelwind.api.server.command;

/**
 * This class is implemented by command executors.
 */
public interface CommandExecutor {
    /**
     * Executes the command for a specified {@code source}.
     * @param source the source of the command
     * @param args the arguments for the command
     */
    void execute(CommandExecutorSource source, String[] args) throws Exception;
}
