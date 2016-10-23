package com.voxelwind.server.command.mcpedata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class CommandParameter {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String target;
    @JsonProperty("enum_values")
    private final List<String> enumValues;
}
