package com.voxelwind.api.plugin;

import com.voxelwind.api.server.Server;
import org.slf4j.Logger;

/**
 * This class encapsulates several services that Voxelwind makes available to all plugins.
 */
public interface PluginContainer {
    Logger getLogger();

    Server getServer();
}
