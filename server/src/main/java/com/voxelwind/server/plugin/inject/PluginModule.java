package com.voxelwind.server.plugin.inject;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.voxelwind.api.plugin.PluginContainer;
import com.voxelwind.api.server.Server;
import org.slf4j.Logger;

public class PluginModule extends AbstractModule {
    private final PluginContainer container;

    public PluginModule(PluginContainer container) {
        this.container = Preconditions.checkNotNull(container, "container");
    }

    @Override
    protected void configure() {
        bind(Logger.class).toInstance(container.getLogger());
        bind(Server.class).toInstance(container.getServer());
    }
}
