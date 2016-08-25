package com.voxelwind.server.plugin.loader;

import com.voxelwind.api.plugin.PluginContainer;

import java.nio.file.Path;
import java.util.Collection;

public class VoxelwindPluginContainer extends VoxelwindPluginDescription implements PluginContainer {
    private final Object plugin;

    public VoxelwindPluginContainer(String id, String author, String version, String website, Collection<String> dependencies, Collection<String> softDependencies, Path path, Object plugin) {
        super(id, author, version, website, dependencies, softDependencies, path);
        this.plugin = plugin;
    }

    @Override
    public Object getPlugin() {
        return plugin;
    }
}
