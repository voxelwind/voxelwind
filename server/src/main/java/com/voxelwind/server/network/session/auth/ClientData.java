package com.voxelwind.server.network.session.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class ClientData {
    @JsonProperty("ClientRandomId")
    private final long clientRandomId;
    @JsonProperty("ServerAddress")
    private final String serverAddress;
    @JsonProperty("SkinData")
    private final String skinData;
    @JsonProperty("SkinId")
    private final String skinId;
}
