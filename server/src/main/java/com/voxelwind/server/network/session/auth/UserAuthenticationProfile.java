package com.voxelwind.server.network.session.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class UserAuthenticationProfile {
    @JsonProperty
    private String displayName;
    @JsonProperty
    private UUID identity;
    @JsonProperty(value = "XUID")
    private Long xuid;
}
