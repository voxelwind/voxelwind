package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.level.util.Attribute;
import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

import java.util.Collection;

/**
 * Created by andrew on 8/13/16.
 */
public class McpeAddEntity implements RakNetPackage {
    private long entityId;
    private int entityType;
    private Vector3f position;
    private Vector3f velocity;
    private float yaw;
    private float pitch;
    // TODO: Attributes and metadata.
    private Collection<Attribute> attributes;

    @Override
    public void decode(ByteBuf buffer) {

    }

    @Override
    public void encode(ByteBuf buffer) {

    }
}
