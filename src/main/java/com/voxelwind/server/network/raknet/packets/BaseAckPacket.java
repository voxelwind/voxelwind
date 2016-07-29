package com.voxelwind.server.network.raknet.packets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.voxelwind.server.network.raknet.RakNetPackage;
import io.netty.buffer.ByteBuf;

import java.nio.ByteOrder;
import java.util.*;

public abstract class BaseAckPacket implements RakNetPackage {
    private final List<Range<Integer>> ids = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        short size = buffer.readShort();
        for (int i = 0; i < size; i++) {
            boolean isSingleton = buffer.readBoolean();
            int lower = buffer.order(ByteOrder.LITTLE_ENDIAN).readMedium();
            if (isSingleton) {
                ids.add(Range.singleton(lower));
            } else {
                int upper = buffer.order(ByteOrder.LITTLE_ENDIAN).readMedium();
                ids.add(Range.closed(lower, upper));
            }
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeShort(ids.size());
        for (Range<Integer> id : ids) {
            boolean singleton = id.lowerEndpoint().equals(id.upperEndpoint());
            buffer.writeBoolean(singleton);
            buffer.order(ByteOrder.LITTLE_ENDIAN).writeMedium(id.lowerEndpoint());
            if (!singleton) {
                buffer.order(ByteOrder.LITTLE_ENDIAN).writeMedium(id.upperEndpoint());
            }
        }
    }

    public List<Range<Integer>> getIds() {
        return ids;
    }

    public static List<Range<Integer>> intoRanges(Collection<Integer> knownIds) {
        if (knownIds.isEmpty()) {
            throw new NoSuchElementException();
        }

        List<Range<Integer>> ranges = new ArrayList<>();
        List<Integer> ids = new ArrayList<>(knownIds);
        Collections.sort(ids);

        if (ids.size() == 1) {
            return ImmutableList.of(Range.singleton(ids.get(0)));
        }

        int start = ids.get(0);
        int cur = start;

        for (Integer id : ids.subList(1, ids.size())) {
            if (cur + 1 == id) {
                cur = id;
            } else {
                ranges.add(Range.closed(start, cur));
                start = id;
                cur = id;
            }
        }

        if (start == cur) {
            ranges.add(Range.singleton(start));
        } else {
            ranges.add(Range.closed(start, cur));
        }

        return ranges;
    }
}
