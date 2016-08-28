package com.voxelwind.server.command;

import com.voxelwind.api.server.command.CommandExecutorSource;
import com.voxelwind.api.server.command.CommandNotFoundException;
import org.junit.Test;

import javax.annotation.Nonnull;

import static org.junit.Assert.*;

public class VoxelwindCommandManagerTest {
    @Test
    public void executeCommand() throws Exception {
        VoxelwindCommandManager manager = new VoxelwindCommandManager();
        manager.register("test", (source, args) -> {});
        manager.executeCommand(new TestCS(), "test");
    }

    @Test
    public void executeCommandOddTrimming() throws Exception {
        VoxelwindCommandManager manager = new VoxelwindCommandManager();
        manager.register("test", (source, args) -> assertEquals("Command not properly trimmed", 0, args.length));
        manager.executeCommand(new TestCS(), "test  ");
    }

    @Test(expected = CommandNotFoundException.class)
    public void unregister() throws Exception {
        VoxelwindCommandManager manager = new VoxelwindCommandManager();
        manager.register("test", (source, args) -> fail("Command not unregistered"));
        manager.unregister("test");
        manager.executeCommand(new TestCS(), "test");
    }

    private class TestCS implements CommandExecutorSource {
        @Nonnull
        @Override
        public String getName() {
            return "Hi";
        }
    }
}