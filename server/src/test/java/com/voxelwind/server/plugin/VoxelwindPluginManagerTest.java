package com.voxelwind.server.plugin;

import com.google.common.collect.ImmutableList;
import com.voxelwind.api.plugin.PluginDescription;
import com.voxelwind.server.plugin.loader.VoxelwindPluginDescription;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class VoxelwindPluginManagerTest {
    private static final PluginDescription DEPENDENT = new VoxelwindPluginDescription(
            "example", "tuxed", "0.1", "voxelwind.com", ImmutableList.of("example2"), ImmutableList.of(), null
    );
    private static final PluginDescription DEPENDENT_S_DEPENDENCY = new VoxelwindPluginDescription(
            "example2", "tuxed", "0.1", "voxelwind.com", ImmutableList.of(), ImmutableList.of(), null
    );
    private static final PluginDescription NO_DEPENDENCY = new VoxelwindPluginDescription(
            "and-again", "tuxed", "0.1", "voxelwind.com", ImmutableList.of(), ImmutableList.of(), null
    );
    private static final PluginDescription SOFT_DEPENDENCY_EXISTS = new VoxelwindPluginDescription(
            "soft", "tuxed", "0.1", "voxelwind.com", ImmutableList.of(), ImmutableList.of("example"), null
    );
    private static final PluginDescription SOFT_DEPENDENCY_DOES_NOT_EXIST = new VoxelwindPluginDescription(
            "fluffy", "tuxed", "0.1", "voxelwind.com", ImmutableList.of(), ImmutableList.of("i-dont-exist"), null
    );

    private static final List<PluginDescription> EXPECTED = ImmutableList.of(
            DEPENDENT_S_DEPENDENCY,
            NO_DEPENDENCY,
            SOFT_DEPENDENCY_DOES_NOT_EXIST,
            DEPENDENT,
            SOFT_DEPENDENCY_EXISTS
    );

    @Test
    public void sortDescriptions() throws Exception {
        List<PluginDescription> descriptionList = new ArrayList<>();
        descriptionList.add(DEPENDENT);
        descriptionList.add(DEPENDENT_S_DEPENDENCY);
        descriptionList.add(NO_DEPENDENCY);
        descriptionList.add(SOFT_DEPENDENCY_DOES_NOT_EXIST);
        descriptionList.add(SOFT_DEPENDENCY_EXISTS);

        VoxelwindPluginManager manager = new VoxelwindPluginManager(null);
        Assert.assertEquals("Load order found is invalid", EXPECTED, manager.sortDescriptions(descriptionList));
    }
}