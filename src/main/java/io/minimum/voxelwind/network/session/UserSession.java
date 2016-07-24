package io.minimum.voxelwind.network.session;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import io.minimum.voxelwind.VoxelwindServer;
import io.minimum.voxelwind.network.handler.NetworkPacketHandler;
import io.minimum.voxelwind.network.mcpe.annotations.ForceClearText;
import io.minimum.voxelwind.network.mcpe.packets.McpeBatch;
import io.minimum.voxelwind.network.mcpe.annotations.BatchDisallowed;
import io.minimum.voxelwind.network.raknet.RakNetPackage;
import io.minimum.voxelwind.network.raknet.datagrams.EncapsulatedRakNetPacket;
import io.minimum.voxelwind.network.raknet.datagrams.RakNetDatagram;
import io.minimum.voxelwind.network.raknet.enveloped.AddressedRakNetDatagram;
import io.minimum.voxelwind.network.raknet.enveloped.DirectAddressedRakNetPacket;
import io.minimum.voxelwind.network.raknet.packets.AckPacket;
import io.minimum.voxelwind.network.util.EncryptionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UserSession {
    private static final Logger LOGGER = LogManager.getLogger(UserSession.class);

    private final InetSocketAddress remoteAddress;
    private String username;
    private final short mtu;
    private NetworkPacketHandler handler;
    private volatile SessionState state = SessionState.INITIAL_CONNECTION;
    private final AtomicLong lastKnownUpdate = new AtomicLong(System.currentTimeMillis());
    private final ConcurrentMap<Short, Queue<EncapsulatedRakNetPacket>> splitPackets = new ConcurrentHashMap<>();
    private final AtomicInteger datagramSequenceGenerator = new AtomicInteger();
    private final AtomicInteger reliabilitySequenceGenerator = new AtomicInteger();
    private final AtomicInteger orderSequenceGenerator = new AtomicInteger();
    private final Queue<RakNetPackage> currentlyQueued = new ConcurrentLinkedQueue<>();
    private final Queue<Integer> ackQueue = new ArrayDeque<>();
    private final ConcurrentMap<Integer, SentDatagram> datagramAcks = new ConcurrentHashMap<>();
    private final Channel channel;
    private final VoxelwindServer server;
    private Cipher encryptionCipher;
    private Cipher decryptionCipher;

    public UserSession(InetSocketAddress remoteAddress, short mtu, NetworkPacketHandler handler, Channel channel, VoxelwindServer server) {
        this.remoteAddress = remoteAddress;
        this.mtu = mtu;
        this.handler = handler;
        this.channel = channel;
        this.server = server;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public short getMtu() {
        return mtu;
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public AtomicLong getLastKnownUpdate() {
        return lastKnownUpdate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public NetworkPacketHandler getHandler() {
        return handler;
    }

    public void setHandler(NetworkPacketHandler handler) {
        this.handler = handler;
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
        Queue<EncapsulatedRakNetPacket> packets = splitPackets.computeIfAbsent(packet.getPartId(), (k) -> new ConcurrentLinkedQueue<>());
        packets.add(packet);

        // Is the packet collection complete?
        BitSet found = new BitSet();
        for (EncapsulatedRakNetPacket netPacket : packets) {
            if (found.get(netPacket.getPartIndex()))
                throw new RuntimeException("Multiple parts found for " + netPacket.getPartId() + " at #" + netPacket.getPartId());

            found.set(netPacket.getPartIndex(), true);
        }

        for (int i = 0; i < packet.getPartCount(); i++) {
            if (!found.get(i)) {
                return Optional.empty();
            }
        }

        // It is, concatenate all packet data and return it.
        splitPackets.remove(packet.getPartId());

        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        List<EncapsulatedRakNetPacket> sorted = new ArrayList<>(packets);
        sorted.sort((p, p1) -> Integer.compare(p.getPartIndex(), p1.getPartIndex()));
        for (EncapsulatedRakNetPacket netPacket : sorted) {
            buf.writeBytes(netPacket.getBuffer());
        }
        return Optional.of(buf);
    }

    public void onAck(List<Range<Integer>> acked) {
        for (Range<Integer> range : acked) {
            for (Integer integer : ContiguousSet.create(range, DiscreteDomain.integers())) {
                SentDatagram datagram = datagramAcks.remove(integer);
                if (datagram != null) {
                    LOGGER.error("Datagram " + datagram.getDatagram().getDatagramSequenceNumber() + " has been ACKed");
                    datagram.tryRelease();
                }
            }
        }
    }

    public void onNak(List<Range<Integer>> acked) {
        for (Range<Integer> range : acked) {
            for (Integer integer : ContiguousSet.create(range, DiscreteDomain.integers())) {
                SentDatagram datagram = datagramAcks.remove(integer);
                if (datagram != null) {
                    LOGGER.error("Must resend datagram " + datagram.getDatagram().getDatagramSequenceNumber() + " due to NAK");
                    datagram.refreshForResend(this);
                    channel.write(datagram, channel.voidPromise());
                    datagramAcks.put(datagram.getDatagram().getDatagramSequenceNumber(), datagram);
                }
            }
        }

        channel.flush();
    }

    public void queuePackageForSend(RakNetPackage netPackage) {
        currentlyQueued.add(netPackage);
    }

    public void sendUrgentPackage(RakNetPackage netPackage) {
        internalSendPackage(netPackage);
        channel.flush();
    }

    private void internalSendPackage(RakNetPackage netPackage) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        netPackage.encode(buf);

        ByteBuf toEncapsulate;
        if (!netPackage.getClass().isAnnotationPresent(ForceClearText.class) && encryptionCipher != null) {
            toEncapsulate = PooledByteBufAllocator.DEFAULT.buffer();
            try {
                EncryptionUtil.aesEncrypt(buf, toEncapsulate, encryptionCipher);
            } catch (GeneralSecurityException e) {
                toEncapsulate.release();
                throw new RuntimeException("Unable to encipher package", e);
            } finally {
                buf.release();
            }
        } else {
            toEncapsulate = buf;
        }

        List<EncapsulatedRakNetPacket> addressed = EncapsulatedRakNetPacket.encapsulatePackage(toEncapsulate, this);
        List<RakNetDatagram> datagrams = new ArrayList<>();
        RakNetDatagram datagram = new RakNetDatagram();
        datagram.setDatagramSequenceNumber(datagramSequenceGenerator.incrementAndGet());
        for (EncapsulatedRakNetPacket packet : addressed) {
            if (!datagram.tryAddPacket(packet, mtu)) {
                datagrams.add(datagram);
                datagram = new RakNetDatagram();
                datagram.setDatagramSequenceNumber(datagramSequenceGenerator.incrementAndGet());
                if (!datagram.tryAddPacket(packet, mtu)) {
                    throw new RuntimeException("Packet too large to fit in MTU (size: " + packet.totalLength() + ", MTU: " + mtu + ")");
                }
            }
        }

        datagrams.add(datagram);

        for (RakNetDatagram netDatagram : datagrams) {
            System.out.println("[Attempt Send] " + netDatagram);
            channel.write(new AddressedRakNetDatagram(netDatagram, remoteAddress), channel.voidPromise());
            datagramAcks.put(datagram.getDatagramSequenceNumber(), new SentDatagram(datagram));
        }
    }

    public void sendDirectPackage(RakNetPackage netPackage) {
        channel.writeAndFlush(new DirectAddressedRakNetPacket(netPackage, remoteAddress));
    }

    public void onTick() {
        sendQueued();
        sendAckQueue();
    }

    private void sendQueued() {
        RakNetPackage netPackage;
        McpeBatch batch = new McpeBatch();
        while ((netPackage = currentlyQueued.poll()) != null) {
            if (netPackage.getClass().isAnnotationPresent(BatchDisallowed.class)) {
                // We hit a un-batchable packet. Send the current batch and then send the un-batchable packet.
                if (!batch.getPackages().isEmpty()) {
                    internalSendPackage(batch);
                    LOGGER.error("Sent batch before batch-exempt packet; " + batch.getPackages().size() + " packages sent.");
                    batch = new McpeBatch();
                }

                internalSendPackage(netPackage);

                if (netPackage instanceof McpeBatch) {
                    try {
                        // Delay things a tiny bit
                        // TODO: Investigate other solutions, including batching
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        LOGGER.error("Interrupted", e);
                    }
                }

                continue;
            }

            batch.getPackages().add(netPackage);
        }

        if (!batch.getPackages().isEmpty()) {
            internalSendPackage(batch);
            LOGGER.error("Batch sent; " + batch.getPackages().size() + " packages sent.");
        }
        channel.flush();
    }

    public void enqueueAck(int ack) {
        synchronized (ackQueue) {
            ackQueue.add(ack);
        }
    }

    private void sendAckQueue() {
        List<Range<Integer>> ranges;
        synchronized (ackQueue) {
            if (ackQueue.isEmpty())
                return;

            ranges = AckPacket.intoRanges(ackQueue);
            ackQueue.clear();
        }

        System.out.println("Doing ACK: " + ranges);

        AckPacket packet = new AckPacket();
        packet.getIds().addAll(ranges);
        sendDirectPackage(packet);
    }

    protected void beginEncrypting(byte[] sharedSecret) {
        byte[] iv = Arrays.copyOf(sharedSecret, 16);
        SecretKey key = new SecretKeySpec(sharedSecret, "AES");
        try {
            encryptionCipher = Cipher.getInstance("AES/CFB8/NoPadding");
            decryptionCipher = Cipher.getInstance("AES/CFB8/NoPadding");

            encryptionCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            decryptionCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException("Unable to initialize ciphers", e);
        }
    }

    public boolean isEncrypted() {
        return encryptionCipher != null;
    }

    public void close() {
        server.getSessionManager().remove(remoteAddress);
    }
}
