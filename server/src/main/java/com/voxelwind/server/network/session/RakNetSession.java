package com.voxelwind.server.network.session;

import com.google.common.base.Preconditions;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.raknet.datagrams.EncapsulatedRakNetPacket;
import com.voxelwind.server.network.raknet.datagrams.RakNetDatagram;
import com.voxelwind.server.network.raknet.datastructs.IntRange;
import com.voxelwind.server.network.raknet.enveloped.AddressedRakNetDatagram;
import com.voxelwind.server.network.raknet.enveloped.DirectAddressedRakNetPacket;
import com.voxelwind.server.network.raknet.packets.AckPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RakNetSession {
    private static final Logger LOGGER = LogManager.getLogger(RakNetSession.class);
    private final InetSocketAddress remoteAddress;
    private final short mtu;
    private final AtomicLong lastKnownUpdate = new AtomicLong(System.currentTimeMillis());
    private final ConcurrentMap<Short, SplitPacketHelper> splitPackets = new ConcurrentHashMap<>();
    private final AtomicInteger datagramSequenceGenerator = new AtomicInteger();
    private final AtomicInteger reliabilitySequenceGenerator = new AtomicInteger();
    private final AtomicInteger orderSequenceGenerator = new AtomicInteger();
    private final Set<Integer> ackQueue = new HashSet<>();
    private final ConcurrentMap<Integer, SentDatagram> datagramAcks = new ConcurrentHashMap<>();
    private final Channel channel;
    private final VoxelwindServer server;
    private boolean closed = false;

    public RakNetSession(InetSocketAddress remoteAddress, short mtu, Channel channel, VoxelwindServer server) {
        this.remoteAddress = remoteAddress;
        this.mtu = mtu;
        this.channel = channel;
        this.server = server;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public short getMtu() {
        return mtu;
    }

    public AtomicLong getLastKnownUpdate() {
        return lastKnownUpdate;
    }

    public AtomicInteger getDatagramSequenceGenerator() {
        return datagramSequenceGenerator;
    }

    public AtomicInteger getReliabilitySequenceGenerator() {
        return reliabilitySequenceGenerator;
    }

    public AtomicInteger getOrderSequenceGenerator() {
        return orderSequenceGenerator;
    }

    public Optional<ByteBuf> addSplitPacket(EncapsulatedRakNetPacket packet) {
        checkForClosed();
        SplitPacketHelper helper = splitPackets.computeIfAbsent(packet.getPartId(), (k) -> new SplitPacketHelper());
        Optional<ByteBuf> result = helper.add(packet);
        if (result.isPresent()) {
            splitPackets.remove(packet.getPartId());
        }
        return result;
    }

    public void onAck(List<IntRange> acked) {
        checkForClosed();
        for (IntRange range : acked) {
            for (int i = range.getStart(); i <= range.getEnd(); i++) {
                SentDatagram datagram = datagramAcks.remove(i);
                if (datagram != null) {
                    datagram.tryRelease();
                }
            }
        }
    }

    public void onNak(List<IntRange> acked) {
        checkForClosed();
        for (IntRange range : acked) {
            for (int i = range.getStart(); i <= range.getEnd(); i++) {
                SentDatagram datagram = datagramAcks.get(i);
                if (datagram != null) {
                    LOGGER.warn("Must resend datagram " + datagram.getDatagram().getDatagramSequenceNumber() + " due to NAK");
                    datagram.refreshForResend();
                    channel.write(datagram, channel.voidPromise());
                }
            }
        }

        channel.flush();
    }

    protected void internalSendRakNetPackage(ByteBuf encoded) {
        List<EncapsulatedRakNetPacket> addressed = EncapsulatedRakNetPacket.encapsulatePackage(encoded, this);
        List<RakNetDatagram> datagrams = new ArrayList<>();
        for (EncapsulatedRakNetPacket packet : addressed) {
            RakNetDatagram datagram = new RakNetDatagram();
            datagram.setDatagramSequenceNumber(datagramSequenceGenerator.getAndIncrement());
            if (!datagram.tryAddPacket(packet, mtu)) {
                throw new IllegalArgumentException("Packet too large to fit in MTU (size: " + packet.totalLength() + ", MTU: " + mtu + ")");
            }
            datagrams.add(datagram.retain()); // retain in case we need to resend it
        }

        for (RakNetDatagram netDatagram : datagrams) {
            channel.write(new AddressedRakNetDatagram(netDatagram, remoteAddress), channel.voidPromise());
            datagramAcks.put(netDatagram.getDatagramSequenceNumber(), new SentDatagram(netDatagram));
        }
    }

    public void sendDirectPackage(RakNetPackage netPackage) {
        checkForClosed();
        channel.writeAndFlush(new DirectAddressedRakNetPacket(netPackage, remoteAddress));
    }

    public void onTick() {
        if (closed) {
            return;
        }

        sendAckQueue();
        resendStalePackets();
        cleanSplitPackets();
    }

    private void cleanSplitPackets() {
        for (Iterator<SplitPacketHelper> it = splitPackets.values().iterator(); it.hasNext(); ) {
            SplitPacketHelper sph = it.next();
            if (sph.expired()) {
                sph.release();
                it.remove();
            }
        }
    }

    private void resendStalePackets() {
        for (SentDatagram datagram : datagramAcks.values()) {
            if (datagram.isStale()) {
                LOGGER.warn("Datagram " + datagram.getDatagram().getDatagramSequenceNumber() + " for " + remoteAddress + " is stale, resending!");
                datagram.refreshForResend();
                channel.write(new AddressedRakNetDatagram(datagram.getDatagram(), remoteAddress));
            }
        }
        channel.flush();
    }

    public void touch() {
        lastKnownUpdate.set(System.currentTimeMillis());
    }

    public void enqueueAck(int ack) {
        checkForClosed();

        synchronized (ackQueue) {
            ackQueue.add(ack);
        }
    }

    private void sendAckQueue() {
        List<IntRange> ranges;
        synchronized (ackQueue) {
            if (ackQueue.isEmpty())
                return;

            ranges = AckPacket.intoRanges(ackQueue);
            ackQueue.clear();
        }

        AckPacket packet = new AckPacket();
        packet.getIds().addAll(ranges);
        sendDirectPackage(packet);
    }

    protected void close() {
        checkForClosed();
        closed = true;

        // Perform resource clean up.
        splitPackets.values().forEach(SplitPacketHelper::release);
        splitPackets.clear();

        datagramAcks.values().forEach(SentDatagram::tryRelease);
        datagramAcks.clear();
    }

    void checkForClosed() {
        Preconditions.checkState(!closed, "Session already closed");
    }

    public boolean isClosed() {
        return closed;
    }

    public VoxelwindServer getServer() {
        return server;
    }

    public Channel getChannel() {
        return channel;
    }
}
