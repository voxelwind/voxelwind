package com.voxelwind.server.plugin.loader;

import com.voxelwind.api.plugin.PluginDescription;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public class VoxelwindPluginDescription implements PluginDescription {
    private final String id;
    private final String author;
    private final String version;
    private final String website;
    private final Collection<String> dependencies;
    private final Collection<String> softDependencies;
    private final Path path;

    public VoxelwindPluginDescription(String id, String author, String version, String website, Collection<String> dependencies, Collection<String> softDependencies, Path path) {
        this.id = id;
        this.author = author;
        this.version = version;
        this.website = website;
        this.dependencies = dependencies;
        this.softDependencies = softDependencies;
        this.path = path;
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
    public String getWebsite() {
        return website;
    }

    @Override
    public Collection<String> getDependencies() {
        return dependencies;
    }

    @Override
    public Collection<String> getSoftDependencies() {
        return softDependencies;
    }

    @Override
    public Optional<Path> getPath() {
        return Optional.ofNullable(path);
    }

    @Override
    public String toString() {
        return "VoxelwindPluginDescription{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", version='" + version + '\'' +
                ", website='" + website + '\'' +
                ", dependencies=" + dependencies +
                ", softDependencies=" + softDependencies +
                ", path=" + path +
                '}';
    }
}
