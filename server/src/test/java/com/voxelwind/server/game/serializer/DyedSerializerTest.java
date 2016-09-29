package com.voxelwind.server.game.serializer;

import com.voxelwind.api.game.item.data.Dyed;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.util.DyeColor;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DyedSerializerTest extends SerializerTestBase {
    @Test
    public void checkNBTOutput() throws Exception {
        VoxelwindItemStack itemStack = new VoxelwindItemStack(BlockTypes.WOOL, 1, Dyed.of(DyeColor.BLACK));

        short data = MetadataSerializer.serializeMetadata(itemStack);
        assertEquals(data, 15);
    }
}