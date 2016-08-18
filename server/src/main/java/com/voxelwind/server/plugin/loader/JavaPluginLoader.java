package com.voxelwind.server.plugin.loader;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.voxelwind.api.plugin.InvalidPluginException;
import com.voxelwind.api.plugin.PluginContainer;
import com.voxelwind.api.plugin.PluginDescription;
import com.voxelwind.api.plugin.PluginLoader;
import com.voxelwind.api.server.Server;
import com.voxelwind.server.plugin.PluginClassLoader;
import com.voxelwind.server.plugin.inject.PluginModule;
import com.voxelwind.server.plugin.loader.java.JavaVoxelwindPluginDescription;
import com.voxelwind.server.plugin.loader.java.PluginClassVisitor;
import com.voxelwind.server.plugin.loader.java.PluginInformation;
import org.objectweb.asm.ClassReader;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class JavaPluginLoader implements PluginLoader {
    private final Server server;

    public JavaPluginLoader(Server server) {
        this.server = server;
    }

    @Nonnull
    @Override
    public PluginDescription loadPlugin(Path path) throws Exception {
        try (JarInputStream in = new JarInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
            Manifest manifest = in.getManifest();
            if (manifest == null) {
                throw new IllegalArgumentException("JAR does not contain a manifest.");
            }

            JarEntry entry;
            while ((entry = in.getNextJarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                Optional<PluginInformation> information = scan(in);
                if (information.isPresent()) {
                    return new JavaVoxelwindPluginDescription(information.get().getId(),
                            information.get().getAuthor(), information.get().getVersion(), ImmutableList.copyOf(information.get().getDependencies()),
                            ImmutableList.copyOf(information.get().getSoftDependencies()), path, information.get().getClassName());
                }
            }
        }

        throw new InvalidPluginException("No main class found");
    }

    @Nonnull
    @Override
    public PluginContainer createPlugin(PluginDescription description) throws Exception {
        if (!(description instanceof JavaVoxelwindPluginDescription)) {
            throw new IllegalArgumentException("Description provided isn't from the Java plugin loader.");
        }

        Optional<Path> path = description.getPath();
        if (!path.isPresent()) {
            throw new IllegalArgumentException("No path in plugin description.");
        }

        return new VoxelwindPluginContainer(description.getId(), description.getAuthor(), description.getVersion(),
                description.getDependencies(), description.getSoftDependencies(), path.get(),
                createPlugin(path.get(), (JavaVoxelwindPluginDescription) description));
    }

    private Object createPlugin(Path path, JavaVoxelwindPluginDescription description) throws MalformedURLException, ClassNotFoundException {
        PluginClassLoader loader = new PluginClassLoader(
                new URL[] { path.toUri().toURL() }
        );

        Class clz = loader.loadClass(description.getClassName().replace('/', '.'));
        Injector injector = Guice.createInjector(new PluginModule(description, server));

        return injector.getInstance(clz);
    }

    private Optional<PluginInformation> scan(InputStream in) throws IOException {
        ClassReader reader = new ClassReader(in);
        PluginClassVisitor visitor = new PluginClassVisitor();

        reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return visitor.information();
    }
}
