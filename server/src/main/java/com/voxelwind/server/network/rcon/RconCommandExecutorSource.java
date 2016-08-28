package com.voxelwind.server.network.rcon;

import com.voxelwind.api.server.command.sources.RemoteConsoleCommandExecutorSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

class RconCommandExecutorSource implements RemoteConsoleCommandExecutorSource {
    private final List<String> output = new ArrayList<>();
    private final AtomicBoolean acceptingOutput = new AtomicBoolean(true);

    @Nonnull
    @Override
    public String getName() {
        return "RCON";
    }

    @Override
    public void sendMessage(@Nonnull String text) {
        if (acceptingOutput.get()) {
            output.add(text);
        }
    }

    public List<String> getOutput() {
        return output;
    }

    public void stopOutput() {
        acceptingOutput.set(false);
    }
}
