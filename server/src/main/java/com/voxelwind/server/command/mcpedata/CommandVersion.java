package com.voxelwind.server.command.mcpedata;

import lombok.Value;

import java.util.Map;

@Value
public class CommandVersion {
    private final String description;
    private final Map<String, CommandOverload> overloads;
}
