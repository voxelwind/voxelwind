package com.voxelwind.server.command.mcpedata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class CommandOverload {
    private final Input input;

    @Value
    public static class Input {
        private final Map<String, CommandParameter> parameters;
    }

    @Value
    public static class Output {
        @JsonProperty("format_strings")
        private final List<String> formatStrings;
        @JsonProperty
        private final Map<String, CommandParameter> parameters;
    }
}
