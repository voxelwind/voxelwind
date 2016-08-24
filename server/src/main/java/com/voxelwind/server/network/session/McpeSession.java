package com.voxelwind.server.network.session;

import com.google.common.base.Preconditions;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.game.level.VoxelwindLevel;
import com.voxelwind.server.jni.hash.VoxelwindHash;
import com.voxelwind.server.network.Native;
import com.voxelwind.server.network.PacketRegistry;
import com.voxelwind.server.network.handler.NetworkPacketHandler;
import com.voxelwind.server.network.mcpe.annotations.BatchDisallowed;
import com.voxelwind.server.network.mcpe.annotations.DisallowWrapping;
import com.voxelwind.server.network.mcpe.annotations.ForceClearText;
import com.voxelwind.server.network.mcpe.packets.McpeBatch;
import com.voxelwind.server.network.mcpe.packets.McpeDisconnect;
import com.voxelwind.server.network.raknet.RakNetPackage;
import com.voxelwind.server.network.session.auth.ClientData;
import com.voxelwind.server.network.session.auth.UserAuthenticationProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class McpeSession extends RakNetSession {
    private static final Logger LOGGER = LogManager.getLogger(McpeSession.class);
    private final AtomicLong encryptedSentPacketGenerator = new AtomicLong();
    private final Queue<RakNetPackage> currentlyQueued = new ConcurrentLinkedQueue<>();
    private UserAuthenticationProfile authenticationProfile;
    private ClientData clientData;
    private NetworkPacketHandler handler;
    private volatile SessionState state = SessionState.INITIAL_CONNECTION;
    private BungeeCipher encryptionCipher;
    private BungeeCipher decryptionCipher;
    private PlayerSession playerSession;
    private byte[] serverKey;

    public McpeSession(InetSocketAddress remoteAddress, short mtu, NetworkPacketHandler handler, Channel channel, VoxelwindServer server) {
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

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending packet {} to {}", netPackage, getRemoteAddress());
        }

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

        getServer().getSessionManager().remove(getRemoteAddress());

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
        VoxelwindHash hash = Native.hash.newInstance();

        ByteBuf counterBuf = PooledByteBufAllocator.DEFAULT.directBuffer(8);
        ByteBuf keyBuf = PooledByteBufAllocator.DEFAULT.directBuffer(serverKey.length);
        counterBuf.order(ByteOrder.LITTLE_ENDIAN).writeLong(encryptedSentPacketGenerator.getAndIncrement());
        keyBuf.writeBytes(serverKey);

        hash.update(counterBuf);
        hash.update(buf);
        hash.update(keyBuf);
        byte[] digested = hash.digest();

        counterBuf.release();
        keyBuf.release();

        return Arrays.copyOf(digested, 8);
    }

    public PlayerSession getPlayerSession() {
        return playerSession;
    }

    public PlayerSession initializePlayerSession(VoxelwindLevel level) {
        checkForClosed();
        Preconditions.checkState(playerSession == null, "Player session already initialized");

        state = SessionState.CONNECTED;
        playerSession = new PlayerSession(this, level);
        return playerSession;
    }

    public ClientData getClientData() {
        return clientData;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    public void disconnect(@Nonnull String reason) {
        Preconditions.checkNotNull(reason, "reason");
        checkForClosed();

        McpeDisconnect packet = new McpeDisconnect();
        packet.setMessage(reason);
        sendUrgentPackage(packet);

        // Wait a little bit for the packet to be sent and close their session
        getChannel().eventLoop().schedule(() -> {
            if (!isClosed()) {
                close();
            }
        }, 500, TimeUnit.MILLISECONDS);
    }
}
