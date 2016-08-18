package com.voxelwind.server.plugin;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.plugin.Plugin;
import com.voxelwind.api.plugin.PluginManager;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class VoxelwindPluginManager implements PluginManager {
    private final List<Object> plugins = new ArrayList<>();

    @Override
    public Collection<Object> getAllPlugins() {
        return ImmutableList.copyOf(plugins);
    }

    @Override
    public Optional<Object> getPlugin(String id) {
        return plugins.stream()
                .filter(p -> p.getClass().getAnnotation(Plugin.class).id().equals(id))
                .findFirst();
    }

    public void loadPlugins() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("plugins"))) {
            for (Path path : stream) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPlugin(Path file) {

    }
}
