package com.voxelwind.server.network.session.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class UserAuthenticationProfile {
    @JsonProperty
    private String displayName;
    @JsonProperty
    private UUID identity;
    @JsonProperty(value = "XUID")
    private Long xuid;

    public String getDisplayName() {
        return displayName;
    }

    public UUID getIdentity() {
        return identity;
    }

    public Long getXuid() {
        return xuid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setIdentity(UUID identity) {
        this.identity = identity;
    }

    public void setXuid(Long xuid) {
        this.xuid = xuid;
    }
}
