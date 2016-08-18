package com.voxelwind.api.plugin;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * This interface is used for loading plugins.
 */
public interface PluginLoader {
    @Nonnull
    PluginContainer loadPlugin(Path path) throws Exception;
}
