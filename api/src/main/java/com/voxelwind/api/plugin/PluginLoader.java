package com.voxelwind.api.plugin;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * This interface is used for loading plugins.
 */
public interface PluginLoader {
    @Nonnull
    PluginDescription loadPlugin(Path path) throws Exception;

    @Nonnull
    PluginContainer createPlugin(PluginDescription description) throws Exception;
}
