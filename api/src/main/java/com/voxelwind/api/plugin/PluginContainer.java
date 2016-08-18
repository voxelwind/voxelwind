package com.voxelwind.api.plugin;

import java.util.Collection;

public interface PluginContainer {
    /**
     * The ID for this plugin. This should be an alphanumeric name. Slashes are also allowed.
     * @return the ID for this plugin
     */
    String getId();

    /**
     * The author of this plugin.
     * @return the plugin's author
     */
    String getAuthor();

    /**
     * The version of this plugin.
     * @return the version of this plugin
     */
    String getVersion();

    /**
     * The array of plugin IDs that this plugin requires in order to load.
     * @return the dependencies
     */
    Collection<String> getDependencies();

    /**
     * The array of plugin IDs that this plugin optionally depends on.
     * @return the soft dependencies
     */
    Collection<String> getSoftDependencies();
}
