package com.voxelwind.server.network.session;

import com.google.common.base.Preconditions;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.voxelwind.server.network.PacketRegistry;
import com.voxelwind.server.network.handler.NetworkPacketHandler;
import com.voxelwind.server.network.mcpe.annotations.DisallowWrapping;
import com.voxelwind.server.network.mcpe.packets.McpeBatch;
import com.voxelwind.server.network.raknet.datagrams.EncapsulatedRakNetPacket;
import com.voxelwind.server.network.raknet.enveloped.AddressedRakNetDatagram;
import com.voxelwind.server.network.raknet.enveloped.DirectAddressedRakNetPacket;
import com.voxelwind.server.network.raknet.packets.AckPacket;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.network.Native;
import com.voxelwind.server.network.mcpe.annotations.ForceClearText;
import com.voxelwind.server.network.mcpe.annotations.BatchDisallowed;
import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.raknet.datagrams.RakNetDatagram;
import com.voxelwind.server.network.session.auth.UserAuthenticationProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
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
    private UserAuthenticationProfile authenticationProfile;
    private final short mtu;
    private NetworkPacketHandler handler;
    private volatile SessionState state = SessionState.INITIAL_CONNECTION;
    private final AtomicLong lastKnownUpdate = new AtomicLong(System.currentTimeMillis());
    private final ConcurrentMap<Short, SplitPacketHelper> splitPackets = new ConcurrentHashMap<>();
    private final AtomicInteger datagramSequenceGenerator = new AtomicInteger();
    private final AtomicInteger reliabilitySequenceGenerator = new AtomicInteger();
    private final AtomicInteger orderSequenceGenerator = new AtomicInteger();
    private final AtomicLong encryptedSentPacketGenerator = new AtomicLong();
    private final Queue<RakNetPackage> currentlyQueued = new ConcurrentLinkedQueue<>();
    private final Set<Integer> ackQueue = new HashSet<>();
    private final ConcurrentMap<Integer, SentDatagram> datagramAcks = new ConcurrentHashMap<>();
    private final Channel channel;
    private final VoxelwindServer server;
    private BungeeCipher encryptionCipher;
    private BungeeCipher decryptionCipher;
    private boolean closed = false;
    private PlayerSession playerSession;
    private byte[] serverKey;
    private static final ThreadLocal<byte[]> CHECKSUM_BUFFER_LOCAL = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[512];
        }
    };

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

    public UserAuthenticationProfile getAuthenticationProfile() {
        return authenticationProfile;
    }

    public void setAuthenticationProfile(UserAuthenticationProfile authenticationProfile) {
        Preconditions.checkNotNull(authenticationProfile, "authenticationProfile");
        this.authenticationProfile = authenticationProfile;
    }

    public NetworkPacketHandler getHandler() {
        return handler;
    }

    public void setHandler(NetworkPacketHandler handler) {
        checkForClosed();
        Preconditions.checkNotNull(handler, "handler");
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
        checkForClosed();
        SplitPacketHelper helper = splitPackets.computeIfAbsent(packet.getPartId(), (k) -> new SplitPacketHelper());
        Optional<ByteBuf> result = helper.add(packet);
        if (result.isPresent()) {
            splitPackets.remove(packet.getPartId());
        }
        return result;
    }

    public void onAck(List<Range<Integer>> acked) {
        checkForClosed();
        for (Range<Integer> range : acked) {
            for (Integer integer : ContiguousSet.create(range, DiscreteDomain.integers())) {
                SentDatagram datagram = datagramAcks.remove(integer);
                if (datagram != null) {
                    datagram.tryRelease();
                }
            }
        }
    }

    public void onNak(List<Range<Integer>> acked) {
        checkForClosed();
        for (Range<Integer> range : acked) {
            for (Integer integer : ContiguousSet.create(range, DiscreteDomain.integers())) {
                SentDatagram datagram = datagramAcks.get(integer);
                if (datagram != null) {
                    LOGGER.error("Must resend datagram " + datagram.getDatagram().getDatagramSequenceNumber() + " due to NAK");
                    datagram.refreshForResend();
                    channel.write(datagram, channel.voidPromise());
                }
            }
        }

        channel.flush();
    }

    public void addToSendQueue(RakNetPackage netPackage) {
        checkForClosed();
        Preconditions.checkNotNull(netPackage, "netPackage");

        Integer id = PacketRegistry.getId(netPackage);
        Preconditions.checkArgument(id != null, "Package " + netPackage + " has no ID.");

        currentlyQueued.add(netPackage);
    }

    public void sendUrgentPackage(RakNetPackage netPackage) {
        checkForClosed();
        Preconditions.checkNotNull(netPackage, "netPackage");
        internalSendPackage(netPackage);
        channel.flush();
    }

    private void internalSendPackage(RakNetPackage netPackage) {
        Integer id = PacketRegistry.getId(netPackage);
        Preconditions.checkArgument(id != null, "Package " + netPackage + " has no ID.");

        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer();

        System.out.println("[Network Send] " + netPackage);

        ByteBuf toEncapsulate;
        if (encryptionCipher == null || netPackage.getClass().isAnnotationPresent(ForceClearText.class)) {
            if (!netPackage.getClass().isAnnotationPresent(DisallowWrapping.class)) {
                buf.writeByte(0xFE);
            }
            buf.writeByte((id & 0xFF));
            netPackage.encode(buf);

            toEncapsulate = buf;
        } else {
            buf.writeByte((id & 0xFF));
            netPackage.encode(buf);
            buf.writeBytes(generateTrailer(buf));

            toEncapsulate = PooledByteBufAllocator.DEFAULT.directBuffer();
            toEncapsulate.writeByte(0xFE);

            try {
                encryptionCipher.cipher(buf, toEncapsulate);
            } catch (GeneralSecurityException e) {
                toEncapsulate.release();
                throw new RuntimeException("Unable to encipher package", e);
            } finally {
                buf.release();
            }
        }

        List<EncapsulatedRakNetPacket> addressed = EncapsulatedRakNetPacket.encapsulatePackage(toEncapsulate, this);
        List<RakNetDatagram> datagrams = new ArrayList<>();
        for (EncapsulatedRakNetPacket packet : addressed) {
            RakNetDatagram datagram = new RakNetDatagram();
            datagram.setDatagramSequenceNumber(datagramSequenceGenerator.getAndIncrement());
            if (!datagram.tryAddPacket(packet, mtu)) {
                throw new RuntimeException("Packet too large to fit in MTU (size: " + packet.totalLength() + ", MTU: " + mtu + ")");
            }
            datagrams.add(datagram);
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

        sendQueued();
        sendAckQueue();
        resendStalePackets();
    }

    private void sendQueued() {
        RakNetPackage netPackage;
        McpeBatch batch = new McpeBatch();
        while ((netPackage = currentlyQueued.poll()) != null) {
            System.out.println("[Sending] " + netPackage);

            if (netPackage.getClass().isAnnotationPresent(BatchDisallowed.class) ||
                    netPackage.getClass().isAnnotationPresent(ForceClearText.class)) {
                // We hit a un-batchable packet. Send the current batch and then send the un-batchable packet.
                if (!batch.getPackages().isEmpty()) {
                    internalSendPackage(batch);
                    batch = new McpeBatch();
                }

                internalSendPackage(netPackage);

                try {
                    // Delay things a tiny bit
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted", e);
                }

                continue;
            }

            batch.getPackages().add(netPackage);
        }

        if (!batch.getPackages().isEmpty()) {
            internalSendPackage(batch);
        }
        channel.flush();
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

    public void enqueueAck(int ack) {
        checkForClosed();

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

        System.out.println("[ACK] " + ranges);

        AckPacket packet = new AckPacket();
        packet.getIds().addAll(ranges);
        sendDirectPackage(packet);
    }

    protected void enableEncryption(byte[] secretKey) {
        checkForClosed();

        serverKey = secretKey;
        byte[] iv = Arrays.copyOf(secretKey, 16);
        SecretKey key = new SecretKeySpec(secretKey, "AES");
        try {
            encryptionCipher = Native.cipher.newInstance();
            decryptionCipher = Native.cipher.newInstance();

            encryptionCipher.init(true, key, iv);
            decryptionCipher.init(false, key, iv);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Unable to initialize ciphers", e);
        }
    }

    public boolean isEncrypted() {
        return encryptionCipher != null;
    }

    public void close() {
        checkForClosed();

        server.getSessionManager().remove(remoteAddress);
        closed = true;

        // Free native resources if required
        if (encryptionCipher != null) {
            encryptionCipher.free();
        }
        if (decryptionCipher != null) {
            decryptionCipher.free();
        }
    }

    public BungeeCipher getEncryptionCipher() {
        return encryptionCipher;
    }

    public BungeeCipher getDecryptionCipher() {
        return decryptionCipher;
    }

    private void checkForClosed() {
        Preconditions.checkState(!closed, "Session already closed");
    }

    private byte[] generateTrailer(ByteBuf buf) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        digest.update(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(encryptedSentPacketGenerator.getAndIncrement()));
        // TODO: This is bad -  maybe we can make this native code?
        byte[] tempBuf = CHECKSUM_BUFFER_LOCAL.get();
        int readable = buf.readableBytes();
        if (tempBuf.length < readable) {
            CHECKSUM_BUFFER_LOCAL.set(new byte[readable]);
        }
        buf.readBytes(tempBuf);
        digest.update(tempBuf, 0, readable);
        digest.update(serverKey);

        byte[] digested = digest.digest();
        return Arrays.copyOf(digested, 8);
    }

    public boolean isClosed() {
        return closed;
    }

    public PlayerSession getPlayerSession() {
        return playerSession;
    }

    public PlayerSession initializePlayerSession() {
        checkForClosed();
        Preconditions.checkState(playerSession == null, "Player session already initialized");

        playerSession = new PlayerSession(this);
        return playerSession;
    }

    public VoxelwindServer getServer() {
        return server;
    }

    public Channel getChannel() {
        return channel;
    }
}
