package com.voxelwind.server.command;

import com.google.common.base.Preconditions;
import com.voxelwind.api.server.command.*;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VoxelwindCommandManager implements CommandManager {
    private final Map<String, CommandExecutor> commandMap = new ConcurrentHashMap<>();

    @Override
    public void register(@Nonnull String command, @Nonnull CommandExecutor executor) {
        Preconditions.checkNotNull(command, "command");
        Preconditions.checkNotNull(executor, "executor");
        commandMap.put(command, executor);
    }

    @Override
    public void executeCommand(@Nonnull CommandExecutorSource source, @Nonnull String command) throws CommandException {
        Preconditions.checkNotNull(source, "source");
        Preconditions.checkNotNull(command, "command");

        String[] args = command.trim().split(" ");
        CommandExecutor executor = commandMap.get(args[0]);
        if (executor == null) {
            throw new CommandNotFoundException(args[0]);
        }

        try {
            executor.execute(source, Arrays.copyOfRange(args, 1, args.length));
        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

    @Override
    public void unregister(@Nonnull String command) {
        Preconditions.checkNotNull(command, "command");
        commandMap.remove(command);
    }
}
