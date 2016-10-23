package com.voxelwind.server.command.mcpedata;

import lombok.Value;

import java.util.List;

@Value
public class DefinedCommand {
    private final List<CommandVersion> versions;
}
