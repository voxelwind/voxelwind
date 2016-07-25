package com.voxelwind.server.network.handler;

import com.voxelwind.server.network.mcpe.packets.McpeLogin;

public interface NetworkPacketHandler {
    void handle(McpeLogin login);
}
