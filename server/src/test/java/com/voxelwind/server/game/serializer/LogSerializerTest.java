package com.voxelwind.server.game.serializer;

import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.item.data.wood.Log;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.game.util.data.LogDirection;
import com.voxelwind.api.game.util.data.TreeSpecies;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LogSerializerTest extends SerializerTestBase {
    @Test
    public void checkMetadataLog1Simple() throws Exception {
        BlockState state = generateTestBlockState(BlockTypes.WOOD, null, Log.of(TreeSpecies.OAK, LogDirection.VERTICAL));
        short metadata = MetadataSerializer.serializeMetadata(state);
        assertEquals(0, metadata);
    }

    @Test
    public void checkMetadataLog1OakDirectional() throws Exception {
        BlockState state = generateTestBlockState(BlockTypes.WOOD, null, Log.of(TreeSpecies.OAK, LogDirection.NONE));
        short metadata = MetadataSerializer.serializeMetadata(state);
        assertEquals(12, metadata);
    }

    @Test
    public void checkMetadataLog1SpruceDirectional() throws Exception {
        BlockState state = generateTestBlockState(BlockTypes.WOOD, null, Log.of(TreeSpecies.SPRUCE, LogDirection.NONE));
        short metadata = MetadataSerializer.serializeMetadata(state);
        assertEquals(13, metadata);
    }

    @Test
    public void checkMetadataLog2Acaica() throws Exception {
        BlockState state = generateTestBlockState(BlockTypes.ACACIA_WOOD, null, Log.of(TreeSpecies.ACACIA, LogDirection.VERTICAL));
        short metadata = MetadataSerializer.serializeMetadata(state);
        assertEquals(0, metadata);
    }

    @Test
    public void checkMetadataLog2AcaicaDirectional() throws Exception {
        BlockState state = generateTestBlockState(BlockTypes.ACACIA_WOOD, null, Log.of(TreeSpecies.ACACIA, LogDirection.NONE));
        short metadata = MetadataSerializer.serializeMetadata(state);
        assertEquals(12, metadata);
    }

    @Test
    public void checkMetadataLog2DarkOakDirectional() throws Exception {
        BlockState state = generateTestBlockState(BlockTypes.ACACIA_WOOD, null, Log.of(TreeSpecies.DARK_OAK, LogDirection.HORIZONTAL_X));
        short metadata = MetadataSerializer.serializeMetadata(state);
        assertEquals(5, metadata);
    }

    @Test
    public void checkMetadataLog1SimpleDeserialize() throws Exception {
        Metadata metadata = MetadataSerializer.deserializeMetadata(BlockTypes.WOOD, (short) 0);
        assertTrue("Metadata is not of correct instance type", metadata instanceof Log);
        Log log = (Log) metadata;
        assertEquals(TreeSpecies.OAK, log.getSpecies());
        assertEquals(LogDirection.VERTICAL, log.getDirection());
    }

    @Test
    public void checkMetadataLog1OakDirectionalDeserialize() throws Exception {
        Metadata metadata = MetadataSerializer.deserializeMetadata(BlockTypes.WOOD, (short) 12);
        assertTrue("Metadata is not of correct instance type", metadata instanceof Log);
        Log log = (Log) metadata;
        assertEquals(TreeSpecies.OAK, log.getSpecies());
        assertEquals(LogDirection.NONE, log.getDirection());
    }

    @Test
    public void checkMetadataLog1SpruceDirectionalDeserialize() throws Exception {
        Metadata metadata = MetadataSerializer.deserializeMetadata(BlockTypes.WOOD, (short) 5);
        assertTrue("Metadata is not of correct instance type", metadata instanceof Log);
        Log log = (Log) metadata;
        assertEquals(TreeSpecies.SPRUCE, log.getSpecies());
        assertEquals(LogDirection.HORIZONTAL_X, log.getDirection());
    }
}
