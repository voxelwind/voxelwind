package com.voxelwind.api.server.command.sources;

import com.voxelwind.api.server.MessageRecipient;
import com.voxelwind.api.server.command.CommandExecutorSource;

/**
 * This {@link com.voxelwind.api.server.command.CommandExecutorSource} is used when running a command from the RCON console.
 * Output will be captured on a best-effort basis.
 */
public interface RemoteConsoleCommandExecutorSource extends CommandExecutorSource, MessageRecipient {
}
