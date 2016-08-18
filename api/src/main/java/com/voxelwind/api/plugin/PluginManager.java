package com.voxelwind.api.plugin;

import java.util.Collection;
import java.util.Optional;

public interface PluginManager {
    Collection<Object> getAllPlugins();

    Optional<Object> getPlugin(String id);
}
