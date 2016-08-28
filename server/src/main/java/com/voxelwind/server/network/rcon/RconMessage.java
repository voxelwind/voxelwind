package com.voxelwind.server.network.rcon;

import lombok.Value;

@Value
class RconMessage {
    public static final int SERVERDATA_AUTH = 3;
    public static final int SERVERDATA_AUTH_RESPONSE = 2;
    public static final int SERVERDATA_EXECCOMMAND = 2;
    public static final int SERVERDATA_RESPONSE_VALUE = 0;

    private final int id;
    private final int type;
    private final String body;

    public RconMessage(int id, int type, String body) {
        this.id = id;
        this.type = type;
        this.body = body;
    }
}
