package com.voxelwind.server.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.voxelwind.api.server.command.*;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.network.mcpe.packets.McpeAvailableCommands;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashMap;
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

    public McpeAvailableCommands generateAvailableCommandsPacket() {
        ObjectNode baseCommandNode = VoxelwindServer.MAPPER.createObjectNode();
        for (String s : commandMap.keySet()) {
            // Create the basic parameter node:
            ObjectNode parameterNode = VoxelwindServer.MAPPER.createObjectNode();
            parameterNode.put("name", "args");
            parameterNode.put("type", "rawtext");
            parameterNode.put("optional", true);

            ObjectNode parametersNode = VoxelwindServer.MAPPER.createObjectNode();
            parametersNode.putArray("parameters").add(parameterNode);

            ObjectNode overloadNode = VoxelwindServer.MAPPER.createObjectNode();
            overloadNode.set("input", parametersNode);

            ObjectNode overloadsNode = VoxelwindServer.MAPPER.createObjectNode();
            overloadsNode.set("default", overloadNode);

            ObjectNode commandNode = VoxelwindServer.MAPPER.createObjectNode();
            commandNode.put("description", "n/a");
            commandNode.putArray("aliases");
            commandNode.set("overloads", overloadsNode);
            commandNode.put("permission", "any");

            ObjectNode outerCommandNode = VoxelwindServer.MAPPER.createObjectNode();
            outerCommandNode.putObject("versions").set("default", commandNode);
            baseCommandNode.set(s, outerCommandNode);
        }

        McpeAvailableCommands commands = new McpeAvailableCommands();
        try {
            commands.setCommandJson(VoxelwindServer.MAPPER.writeValueAsString(baseCommandNode));
        } catch (JsonProcessingException e) {
            throw new AssertionError(e);
        }

        return commands;
    }
}
