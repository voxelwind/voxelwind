package com.voxelwind.server.game.serializer;

import com.voxelwind.api.game.item.data.wood.Wood;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.util.data.TreeSpecies;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleWoodSerializerTest extends SerializerTestBase {
    @Test
    public void checkMetadataTest() throws Exception {
        VoxelwindItemStack itemStack = new VoxelwindItemStack(BlockTypes.WOOD_PLANKS, 1, Wood.of(TreeSpecies.OAK));
        short metadata = MetadataSerializer.serializeMetadata(itemStack);
        assertEquals(0, metadata);
    }

    @Test
    public void checkMetadataTestAcaicaAboveIsValid() throws Exception {
        VoxelwindItemStack itemStack2 = new VoxelwindItemStack(BlockTypes.WOOD_PLANKS, 1, Wood.of(TreeSpecies.ACACIA));
        short metadata2 = MetadataSerializer.serializeMetadata(itemStack2);
        assertEquals(4, metadata2);
    }
}
