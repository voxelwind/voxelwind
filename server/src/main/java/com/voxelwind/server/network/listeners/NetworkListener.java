package com.voxelwind.server.network.listeners;

public interface NetworkListener {
    boolean bind();

    void close();
}
