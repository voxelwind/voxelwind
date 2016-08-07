package com.voxelwind.server.network.session;

import com.google.common.base.Preconditions;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.level.Level;
import com.voxelwind.server.network.Native;
import com.voxelwind.server.network.PacketRegistry;
import com.voxelwind.server.network.handler.NetworkPacketHandler;
import com.voxelwind.server.network.mcpe.annotations.BatchDisallowed;
import com.voxelwind.server.network.mcpe.annotations.DisallowWrapping;
import com.voxelwind.server.network.mcpe.annotations.ForceClearText;
import com.voxelwind.server.network.mcpe.packets.McpeBatch;
import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.raknet.datagrams.EncapsulatedRakNetPacket;
import com.voxelwind.server.network.raknet.datagrams.RakNetDatagram;
import com.voxelwind.server.network.raknet.datastructs.IntRange;
import com.voxelwind.server.network.raknet.enveloped.AddressedRakNetDatagram;
import com.voxelwind.server.network.raknet.enveloped.DirectAddressedRakNetPacket;
import com.voxelwind.server.network.raknet.packets.AckPacket;
import com.voxelwind.server.network.session.auth.UserAuthenticationProfile;
import io.netty.buffer.ByteBuf;
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

public class UserSession extends RakNetSession {
    private static final Logger LOGGER = LogManager.getLogger(UserSession.class);
    private static final ThreadLocal<byte[]> CHECKSUM_BUFFER_LOCAL = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[512];
        }
    };
    private final AtomicLong encryptedSentPacketGenerator = new AtomicLong();
    private final Queue<RakNetPackage> currentlyQueued = new ConcurrentLinkedQueue<>();
    private UserAuthenticationProfile authenticationProfile;
    private NetworkPacketHandler handler;
    private volatile SessionState state = SessionState.INITIAL_CONNECTION;
    private BungeeCipher encryptionCipher;
    private BungeeCipher decryptionCipher;
    private PlayerSession playerSession;
    private byte[] serverKey;

    public UserSession(InetSocketAddress remoteAddress, short mtu, NetworkPacketHandler handler, Channel channel, VoxelwindServer server) {
        super(remoteAddress, mtu, channel, server);
        this.handler = handler;
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
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
        getChannel().flush();
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
            byte[] trailer = generateTrailer(buf);
            buf.writeBytes(trailer);

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

        internalSendRakNetPackage(toEncapsulate);
    }

    public void onTick() {
        if (isClosed()) {
            return;
        }

        super.onTick();

        sendQueued();
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

        getChannel().flush();
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

    @Override
    public void close() {
        super.close();

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

    private byte[] generateTrailer(ByteBuf buf) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        digest.update(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(encryptedSentPacketGenerator.getAndIncrement()).array());
        // TODO: This is bad -  maybe we can make this native code?
        byte[] tempBuf = CHECKSUM_BUFFER_LOCAL.get();
        int readable = buf.readableBytes();
        if (tempBuf.length < readable) {
            tempBuf = new byte[readable];
            CHECKSUM_BUFFER_LOCAL.set(tempBuf);
        }
        buf.getBytes(0, tempBuf, 0, readable);
        digest.update(tempBuf, 0, readable);
        digest.update(serverKey);

        byte[] digested = digest.digest();
        return Arrays.copyOf(digested, 8);
    }

    public PlayerSession getPlayerSession() {
        return playerSession;
    }

    public PlayerSession initializePlayerSession(Level level) {
        checkForClosed();
        Preconditions.checkState(playerSession == null, "Player session already initialized");

        playerSession = new PlayerSession(this, level);
        return playerSession;
    }
}
