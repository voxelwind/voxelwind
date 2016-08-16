package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

public class McpeUpdateBlock implements RakNetPackage {
    private Vector3i position;
    private byte blockId;
    private byte metadata;

    @Override
    public void decode(ByteBuf buffer) {
        position = McpeUtil.readVector3i(buffer);
        blockId = buffer.readByte();
        metadata = buffer.readByte();
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeVector3i(buffer, position);
        buffer.writeByte(blockId);
        buffer.writeByte(metadata);
    }

    public Vector3i getPosition() {
        return position;
    }

    public void setPosition(Vector3i position) {
        this.position = position;
    }

    public byte getBlockId() {
        return blockId;
    }

    public void setBlockId(byte blockId) {
        this.blockId = blockId;
    }

    public byte getMetadata() {
        return metadata;
    }

    public void setMetadata(byte metadata) {
        this.metadata = metadata;
    }
}
