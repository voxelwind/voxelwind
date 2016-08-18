package com.voxelwind.api.plugin;

import java.util.Collection;

public interface PluginContainer extends PluginDescription {
    /**
     * Returns the actual plugin object.
     * @return the plugin object
     */
    Object getPlugin();
}
