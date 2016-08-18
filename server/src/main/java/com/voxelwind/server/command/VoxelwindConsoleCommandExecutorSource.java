package com.voxelwind.server.command;

import com.voxelwind.api.server.command.ConsoleCommandExecutorSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class VoxelwindConsoleCommandExecutorSource implements ConsoleCommandExecutorSource {
    private static final Logger LOGGER = LogManager.getLogger(VoxelwindConsoleCommandExecutorSource.class);

    @Nonnull
    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public void sendMessage(@Nonnull String text) {
        LOGGER.info(text);
    }
}
