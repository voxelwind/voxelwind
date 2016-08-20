package com.voxelwind.api.server.command.sources;

import com.voxelwind.api.server.MessageRecipient;
import com.voxelwind.api.server.command.CommandExecutorSource;

/**
 * This {@link CommandExecutorSource} is used when running commands directly from the console. It can also be used by
 * plugins as a sort of "super user".
 */
public interface ConsoleCommandExecutorSource extends MessageRecipient, CommandExecutorSource {
}
