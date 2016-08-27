package com.voxelwind.server.plugin.inject;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.voxelwind.api.plugin.PluginDescription;
import com.voxelwind.api.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginModule extends AbstractModule {
    private final PluginDescription description;
    private final Server server;

    public PluginModule(PluginDescription description, Server server) {
        this.description = Preconditions.checkNotNull(description, "description");
        this.server = Preconditions.checkNotNull(server, "server");
    }

    @Override
    protected void configure() {
        bind(Logger.class).toInstance(LoggerFactory.getLogger(description.getId()));
        bind(Server.class).toInstance(server);
        bind(PluginDescription.class).toInstance(description);
    }
}
