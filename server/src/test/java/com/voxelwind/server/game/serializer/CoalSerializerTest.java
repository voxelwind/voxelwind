package com.voxelwind.server.game.serializer;

import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.item.data.Coal;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CoalSerializerTest extends SerializerTestBase {
    @Test
    public void checkNBTOutput() throws Exception {
        VoxelwindItemStack itemStack = new VoxelwindItemStack(ItemTypes.COAL, 1, Coal.CHARCOAL);

        short data = MetadataSerializer.serializeMetadata(itemStack);
        assertEquals(data, 1);
    }
}