package com.voxelwind.server.plugin.loader;

import com.voxelwind.api.plugin.PluginContainer;

import java.util.Collection;

public class VoxelwindPluginContainer implements PluginContainer {
    private final String id;
    private final String author;
    private final String version;
    private final Collection<String> dependencies;
    private final Collection<String> softDependencies;

    public VoxelwindPluginContainer(String id, String author, String version, Collection<String> dependencies, Collection<String> softDependencies) {
        this.id = id;
        this.author = author;
        this.version = version;
        this.dependencies = dependencies;
        this.softDependencies = softDependencies;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Collection<String> getDependencies() {
        return dependencies;
    }

    @Override
    public Collection<String> getSoftDependencies() {
        return softDependencies;
    }
}
