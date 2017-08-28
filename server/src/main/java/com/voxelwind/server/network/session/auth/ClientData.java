package com.voxelwind.server.network.session.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientData {
    @JsonProperty("ADRole")
    private final int adRole;
    @JsonProperty("ClientRandomId")
    private final long clientRandomId;
    @JsonProperty("CurrentInputMode")
    private final int currentInputMode;
    @JsonProperty("DefaultInputMode")
    private final int defaultInputMode;
    @JsonProperty("DeviceModel")
    private final String deviceModel;
    @JsonProperty("DeviceOS")
    private final int deviceOs;
    @JsonProperty("GameVersion")
    private final String gameVersion;
    @JsonProperty("GuiScale")
    private final int guiScale;
    @JsonProperty("LanguageCode")
    private final String languageCode;
    @JsonProperty("ServerAddress")
    private final String serverAddress;
    @JsonProperty("SkinData")
    private final byte[] skinData; // Jackson conveniently deserializes from Base64
    @JsonProperty("SkinId")
    private final String skinId;
    @JsonProperty("TenantId")
    private final String tenantId;
    @JsonProperty("UIProfile")
    private final int uiProfile;
}
