package com.voxelwind.server.plugin;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.voxelwind.api.plugin.PluginContainer;
import com.voxelwind.api.plugin.PluginDescription;
import com.voxelwind.api.plugin.PluginManager;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.plugin.loader.JavaPluginLoader;
import com.voxelwind.server.plugin.util.DirectedAcyclicGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class VoxelwindPluginManager implements PluginManager {
    private static final Logger LOGGER = LogManager.getLogger(VoxelwindPluginManager.class);

    private final Map<String, PluginContainer> plugins = new HashMap<>();
    private final Server server;

    public VoxelwindPluginManager(Server server) {
        this.server = server;
    }

    @Override
    public Collection<PluginContainer> getAllPlugins() {
        return ImmutableList.copyOf(plugins.values());
    }

    @Override
    public Optional<PluginContainer> getPlugin(String id) {
        Preconditions.checkNotNull(id, "id");
        return Optional.ofNullable(plugins.get(id));
    }

    public void loadPlugins(Path directory) throws IOException {
        Preconditions.checkNotNull(directory, "directory");
        Preconditions.checkArgument(Files.isDirectory(directory), "provided path isn't a directory");

        List<PluginDescription> found = new ArrayList<>();
        JavaPluginLoader loader = new JavaPluginLoader(server);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, p -> p.toString().endsWith(".jar"))) {
            for (Path path : stream) {
                try {
                    found.add(loader.loadPlugin(path));
                } catch (Exception e) {
                    LOGGER.error("Unable to enumerate plugin {}", path, e);
                }
            }
        }

        List<PluginDescription> sortedPlugins = sortDescriptions(found);
        // Now load the plugins.
        pluginLoad: for (PluginDescription plugin : sortedPlugins) {
            // Verify dependencies first.
            for (String s : plugin.getDependencies()) {
                Optional<PluginContainer> loadedPlugin = getPlugin(s);
                if (!loadedPlugin.isPresent()) {
                    LOGGER.error("Can't load plugin {} due to missing dependency {}", plugin.getId(), s);
                    continue pluginLoad;
                }
            }

            // Now actually create the plugin.
            PluginContainer pluginObject;
            try {
                pluginObject = loader.createPlugin(plugin);
            } catch (Exception e) {
                LOGGER.error("Can't create plugin {}", plugin.getId(), e);
                continue;
            }

            plugins.put(plugin.getId(), pluginObject);
        }
    }

    @VisibleForTesting
    List<PluginDescription> sortDescriptions(List<PluginDescription> descriptions) {
        // Create our graph, we're going to be using this for Kahn's algorithm.
        DirectedAcyclicGraph<PluginDescription> graph = new DirectedAcyclicGraph<>();

        // Add edges
        for (PluginDescription description : descriptions) {
            graph.add(description);
            for (String s : description.getDependencies()) {
                Optional<PluginDescription> in = descriptions.stream().filter(d -> d.getId().equals(s)).findFirst();
                if (in.isPresent()) {
                    graph.addEdges(description, in.get());
                }
            }

            for (String s : description.getSoftDependencies()) {
                Optional<PluginDescription> in = descriptions.stream().filter(d -> d.getId().equals(s)).findFirst();
                if (in.isPresent()) {
                    graph.addEdges(description, in.get());
                }
            }
        }

        // Now find nodes that have no edges.
        Queue<DirectedAcyclicGraph.Node<PluginDescription>> noEdges = graph.getNodesWithNoEdges();

        // Then actually run Kahn's algorithm.
        List<PluginDescription> sorted = new ArrayList<>();
        while (!noEdges.isEmpty()) {
            DirectedAcyclicGraph.Node<PluginDescription> descriptionNode = noEdges.poll();
            PluginDescription description = descriptionNode.getData();
            sorted.add(description);

            for (DirectedAcyclicGraph.Node<PluginDescription> node : graph.withEdge(description)) {
                node.removeEdge(descriptionNode);
                if (node.getAdjacent().isEmpty()) {
                    if (!noEdges.contains(node)) {
                        noEdges.add(node);
                    }
                }
            }
        }

        if (graph.hasEdges()) {
            throw new IllegalStateException("Plugin circular dependency found: " + graph.toString());
        }

        return sorted;
    }
}
