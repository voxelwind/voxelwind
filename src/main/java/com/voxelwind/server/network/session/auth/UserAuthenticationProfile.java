package com.voxelwind.server.network.session.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class UserAuthenticationProfile {
    @JsonProperty
    private final String displayName;
    @JsonProperty
    private final UUID identity;
    @JsonProperty
    private final String identityPublicKey;
    @JsonProperty(value = "XUID")
    private final Long xuid;

    public UserAuthenticationProfile(String displayName, UUID identity, String identityPublicKey, Long xuid) {
        this.displayName = displayName;
        this.identity = identity;
        this.identityPublicKey = identityPublicKey;
        this.xuid = xuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UUID getIdentity() {
        return identity;
    }

    public String getIdentityPublicKey() {
        return identityPublicKey;
    }

    public Long getXuid() {
        return xuid;
    }
}
