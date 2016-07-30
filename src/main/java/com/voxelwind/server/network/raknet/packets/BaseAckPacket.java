package com.voxelwind.server.network.raknet.packets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.raknet.datastructs.IntRange;
import io.netty.buffer.ByteBuf;

import java.nio.ByteOrder;
import java.util.*;

public abstract class BaseAckPacket implements RakNetPackage {
    private final List<IntRange> ids = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        short size = buffer.readShort();
        for (int i = 0; i < size; i++) {
            boolean isSingleton = buffer.readBoolean();
            int lower = buffer.order(ByteOrder.LITTLE_ENDIAN).readMedium();
            if (isSingleton) {
                ids.add(new IntRange(lower, lower));
            } else {
                int upper = buffer.order(ByteOrder.LITTLE_ENDIAN).readMedium();
                ids.add(new IntRange(lower, upper));
            }
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeShort(ids.size());
        for (IntRange id : ids) {
            boolean singleton = id.getStart() == id.getEnd();
            buffer.writeBoolean(singleton);
            buffer.order(ByteOrder.LITTLE_ENDIAN).writeMedium(id.getStart());
            if (!singleton) {
                buffer.order(ByteOrder.LITTLE_ENDIAN).writeMedium(id.getEnd());
            }
        }
    }

    public List<IntRange> getIds() {
        return ids;
    }

    public static List<IntRange> intoRanges(Collection<Integer> knownIds) {
        if (knownIds.isEmpty()) {
            throw new NoSuchElementException();
        }

        List<IntRange> ranges = new ArrayList<>();
        List<Integer> ids = new ArrayList<>(knownIds);
        if (ids.size() == 1) {
            return ImmutableList.of(new IntRange(ids.get(0)));
        }
        Collections.sort(ids);

        int start = ids.get(0);
        int cur = start;

        for (Integer id : ids.subList(1, ids.size())) {
            if (cur + 1 == id) {
                cur = id;
            } else {
                ranges.add(new IntRange(start, cur));
                start = id;
                cur = id;
            }
        }

        if (start == cur) {
            ranges.add(new IntRange(start));
        } else {
            ranges.add(new IntRange(start, cur));
        }

        return ranges;
    }
}
