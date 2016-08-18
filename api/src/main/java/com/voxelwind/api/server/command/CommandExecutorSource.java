package com.voxelwind.api.server.command;

import javax.annotation.Nonnull;

/**
 * This class is used to indicate a source for command executions. Players and the console implement this interface.
 */
public interface CommandExecutorSource {
    /**
     * Return the source's name.
     * @return the source's name
     */
    @Nonnull
    String getName();
}
