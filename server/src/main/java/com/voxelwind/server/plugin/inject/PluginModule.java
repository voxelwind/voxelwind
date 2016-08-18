package com.voxelwind.server.plugin.inject;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.voxelwind.api.plugin.PluginContainer;
import com.voxelwind.api.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginModule extends AbstractModule {
    private final PluginContainer container;
    private final Server server;

    public PluginModule(PluginContainer container, Server server) {
        this.container = Preconditions.checkNotNull(container, "container");
        this.server = Preconditions.checkNotNull(server, "server");
    }

    @Override
    protected void configure() {
        bind(Logger.class).toInstance(LoggerFactory.getLogger(container.getId()));
        bind(Server.class).toInstance(server);
    }
}
