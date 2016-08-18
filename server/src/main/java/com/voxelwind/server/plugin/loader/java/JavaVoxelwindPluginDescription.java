package com.voxelwind.server.plugin.loader.java;

import com.voxelwind.server.plugin.loader.VoxelwindPluginDescription;

import java.nio.file.Path;
import java.util.Collection;

public class JavaVoxelwindPluginDescription extends VoxelwindPluginDescription {
    private final String className;

    public JavaVoxelwindPluginDescription(String id, String author, String version, Collection<String> dependencies, Collection<String> softDependencies, Path path, String className) {
        super(id, author, version, dependencies, softDependencies, path);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
