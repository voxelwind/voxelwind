package com.voxelwind.server.plugin.loader;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.plugin.InvalidPluginException;
import com.voxelwind.api.plugin.PluginContainer;
import com.voxelwind.api.plugin.PluginLoader;
import com.voxelwind.server.plugin.loader.java.PluginClassVisitor;
import com.voxelwind.server.plugin.loader.java.PluginInformation;
import org.objectweb.asm.ClassReader;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class JavaPluginLoader implements PluginLoader {
    @Nonnull
    @Override
    public PluginContainer loadPlugin(Path path) throws Exception {
        try (JarInputStream in = new JarInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
            Manifest manifest = in.getManifest();
            if (manifest == null) {
                throw new IllegalArgumentException("JAR does not contain a manifest.");
            }

            JarEntry entry = in.getNextJarEntry();

            do {
                if (entry.isDirectory()) {
                    continue;
                }

                Optional<PluginInformation> information = scan(in);
                if (information.isPresent()) {
                    return new VoxelwindPluginContainer(information.get().getId(),
                            information.get().getAuthor(), information.get().getVersion(), ImmutableList.copyOf(information.get().getDependencies()),
                            ImmutableList.copyOf(information.get().getSoftDependencies()));
                }
            } while ((entry = in.getNextJarEntry()) != null);
        }

        throw new InvalidPluginException("No main class found");
    }

    private Optional<PluginInformation> scan(InputStream in) throws IOException {
        ClassReader reader = new ClassReader(in);
        PluginClassVisitor visitor = new PluginClassVisitor();

        reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return visitor.information();
    }
}
