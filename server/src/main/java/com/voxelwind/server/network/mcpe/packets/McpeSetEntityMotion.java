package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class McpeSetEntityMotion implements RakNetPackage {
    private final List<EntityMotion> motionList = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        while (buffer.isReadable()) {
            long entityId = buffer.readLong();
            Vector3f motion = McpeUtil.readVector3f(buffer);
            motionList.add(new EntityMotion(entityId, motion));
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        for (EntityMotion motion : motionList) {
            buffer.writeLong(motion.entityId);
            McpeUtil.writeVector3f(buffer, motion.motion);
        }
    }

    public static class EntityMotion {
        private final long entityId;
        private final Vector3f motion;

        public EntityMotion(long entityId, Vector3f motion) {
            this.entityId = entityId;
            this.motion = motion;
        }

        public long getEntityId() {
            return entityId;
        }

        public Vector3f getMotion() {
            return motion;
        }
    }
}
