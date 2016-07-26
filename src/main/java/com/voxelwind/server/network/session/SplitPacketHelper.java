package com.voxelwind.server.network.session;

import com.google.common.base.Preconditions;
import com.voxelwind.server.network.raknet.datagrams.EncapsulatedRakNetPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.*;

public class SplitPacketHelper {
    private final Queue<EncapsulatedRakNetPacket> packets = new ArrayDeque<>();
    private final BitSet contained = new BitSet();

    public synchronized Optional<ByteBuf> add(EncapsulatedRakNetPacket packet) {
        Preconditions.checkNotNull(packet, "packet");
        Preconditions.checkArgument(packet.isHasSplit(), "packet is not split");

        if (contained.get(packet.getPartIndex())) {
            // Duplicate packet, ignore it.
            return Optional.empty();
        }

        contained.set(packet.getPartIndex(), true);
        packets.add(packet);

        for (int i = 0; i < packet.getPartCount(); i++) {
            if (!contained.get(i))
                return Optional.empty();
        }

        contained.clear();
        List<EncapsulatedRakNetPacket> sortedPackets = new ArrayList<>(packets);
        packets.clear();
        sortedPackets.sort((o1, o2) -> Integer.compare(o1.getPartIndex(), o2.getPartIndex()));

        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        for (EncapsulatedRakNetPacket netPacket : sortedPackets) {
            buf.writeBytes(netPacket.getBuffer());
        }
        return Optional.of(buf);
    }
}
