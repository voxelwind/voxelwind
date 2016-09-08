package com.voxelwind.server.network.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.Preconditions;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.voxelwind.api.server.event.session.SessionLoginEvent;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.jni.CryptoUtil;
import com.voxelwind.server.network.util.NativeCodeFactory;
import com.voxelwind.server.network.mcpe.packets.*;
import com.voxelwind.server.network.raknet.handler.NetworkPacketHandler;
import com.voxelwind.server.network.session.auth.ChainTrustInvalidException;
import com.voxelwind.server.network.session.auth.ClientData;
import com.voxelwind.server.network.session.auth.JwtPayload;
import com.voxelwind.server.network.session.auth.TemporarySession;
import com.voxelwind.server.network.util.EncryptionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class InitialNetworkPacketHandler implements NetworkPacketHandler {
    private static final boolean CAN_USE_ENCRYPTION = CryptoUtil.isJCEUnlimitedStrength() || NativeCodeFactory.cipher.isLoaded();
    private static final String MOJANG_PUBLIC_KEY_BASE64 =
            "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE8ELkixyLcwlZryUQcu1TvPOmI2B7vX83ndnWRUaXm74wFfa5f/lwQNTfrLVHa2PmenpGI6JhIMUJaWZrjmMj90NoKNFSNBuKdm8rYiXsfaz3K36x/1U26HpG0ZxK/V1V";
    private static final PublicKey MOJANG_PUBLIC_KEY;
    private static final Logger LOGGER = LogManager.getLogger(InitialNetworkPacketHandler.class);

    static {
        try {
            MOJANG_PUBLIC_KEY = getKey(MOJANG_PUBLIC_KEY_BASE64);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    private final McpeSession session;

    public InitialNetworkPacketHandler(McpeSession session) {
        this.session = session;
    }

    private static PublicKey getKey(String b64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance("EC").generatePublic(
                new X509EncodedKeySpec(Base64.getDecoder().decode(b64)));
    }

    @Override
    public void handle(McpeLogin packet) {
        JsonNode certData;
        try {
            certData = VoxelwindServer.MAPPER.readTree(packet.getChainData());
        } catch (IOException e) {
            throw new RuntimeException("Certificate JSON can not be read.");
        }

        // Verify the JWT chain data.
        JsonNode certChainData = certData.get("chain");
        if (certChainData.getNodeType() != JsonNodeType.ARRAY) {
            throw new RuntimeException("Certificate data is not valid");
        }

        try {
            JwtPayload payload = validateChainData(certChainData);
            session.setAuthenticationProfile(payload.getExtraData());

            // Get the key to use for encrypting the connection and skin data
            PublicKey key = getKey(payload.getIdentityPublicKey());

            // Set the client data.
            ClientData clientData = getClientData(key, packet.getSkinData());
            session.setClientData(clientData);

            if (CAN_USE_ENCRYPTION && session.getServer().getConfiguration().getXboxAuthentication().isEnabled()) {
                // ...and begin encrypting the connection.
                byte[] token = EncryptionUtil.generateRandomToken();
                byte[] serverKey = EncryptionUtil.getServerKey(key, token);
                session.enableEncryption(serverKey);
                session.sendImmediatePackage(EncryptionUtil.createHandshakePacket(token));
            } else {
                // Will not use encryption - initialize the player's session
                initializePlayerSession();
            }
        } catch (ChainTrustInvalidException e) {
            // If configuration permits us to continue logging the player in, then initialize the player session without
            // encrypting the connection.
            if (!session.getServer().getConfiguration().getXboxAuthentication().isForceAuthentication()) {
                // Fill in all data and try again.
                try {
                    JsonNode authProfilePayload = getPayload(certChainData.get(certChainData.size() - 1).asText());
                    JwtPayload payload = VoxelwindServer.MAPPER.convertValue(authProfilePayload, JwtPayload.class);
                    payload.getExtraData().setXuid(null);
                    session.setAuthenticationProfile(payload.getExtraData());
                    session.setClientData(VoxelwindServer.MAPPER.convertValue(getPayload(packet.getSkinData()), ClientData.class));
                } catch (IOException e1) {
                    // Disconnect the player.
                    LOGGER.error("Unable to initialize player session", e);
                    session.disconnect("Internal server error");
                    return;
                }
                // Since all data is fake, don't bother encrypting the connection.
                initializePlayerSession();
            } else {
                session.disconnect("This server requires that you sign in with Xbox Live.");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to initialize player session", e);
            session.disconnect("Internal server error");
        }
    }

    @Override
    public void handle(McpeClientMagic packet) {
        initializePlayerSession();
    }

    @Override
    public void handle(McpeRequestChunkRadius packet) {
        throw new IllegalStateException("Got unexpected McpeRequestChunkRadius");
    }

    @Override
    public void handle(McpePlayerAction packet) {
        throw new IllegalStateException("Got unexpected McpePlayerAction");
    }

    @Override
    public void handle(McpeAnimate packet) {
        throw new IllegalStateException("Got unexpected McpeAnimate");
    }

    @Override
    public void handle(McpeText packet) {
        throw new IllegalStateException("Got unexpected McpeText");
    }

    @Override
    public void handle(McpeMovePlayer packet) {
        throw new IllegalStateException("Got unexpected McpeMovePlayer");
    }

    @Override
    public void handle(McpeContainerClose packet) {
        throw new IllegalStateException("Got unexpected McpeContainerClose");
    }

    @Override
    public void handle(McpeContainerSetSlot packet) {
        throw new IllegalStateException("Got unexpected McpeContainerSetSlot");
    }

    @Override
    public void handle(McpeMobEquipment packet) {
        throw new IllegalStateException("Got unexpected McpeMobEquipment");
    }

    private void initializePlayerSession() {
        TemporarySession apiSession = new TemporarySession(session);
        SessionLoginEvent event = new SessionLoginEvent(apiSession);
        session.getServer().getEventManager().fire(event);

        if (event.willDisconnect()) {
            session.disconnect(event.getDisconnectReason());
            return;
        }

        McpePlayStatus status = new McpePlayStatus();
        status.setStatus(McpePlayStatus.Status.LOGIN_SUCCESS);
        session.addToSendQueue(status);

        PlayerSession playerSession = session.initializePlayerSession(session.getServer().getDefaultLevel());
        session.setHandler(playerSession.getPacketHandler());
        playerSession.doInitialSpawn();
    }

    private JwtPayload validateChainData(JsonNode data) throws Exception {
        Preconditions.checkArgument(data.getNodeType() == JsonNodeType.ARRAY, "chain data provided is not an array");

        if (CAN_USE_ENCRYPTION) {
            // If encryption support is available, enable authentication.
            PublicKey lastKey = null;
            boolean trustedChain = false;
            for (JsonNode node : data) {
                JWSObject object = JWSObject.parse(node.asText());
                if (!trustedChain) {
                    trustedChain = verify(MOJANG_PUBLIC_KEY, object);
                }
                if (lastKey != null) {
                    if (!verify(lastKey, object)) {
                        throw new JOSEException("Unable to verify key in chain.");
                    }
                }
                lastKey = getKey((String) object.getPayload().toJSONObject().get("identityPublicKey"));
            }

            if (!trustedChain) {
                throw new ChainTrustInvalidException();
            }
        }

        JsonNode payload = getPayload(data.get(data.size() - 1).asText());
        JwtPayload jwtPayload = VoxelwindServer.MAPPER.convertValue(payload, JwtPayload.class);
        if (!CAN_USE_ENCRYPTION || !session.getServer().getConfiguration().getXboxAuthentication().isEnabled()) {
            // Not authenticated, don't allow faking the XUID.
            jwtPayload.getExtraData().setXuid(null);
        }
        return jwtPayload;
    }

    private ClientData getClientData(PublicKey key, String clientData) throws Exception {
        if (CAN_USE_ENCRYPTION) {
            JWSObject object = JWSObject.parse(clientData);
            if (!verify(key, object)) {
                throw new IllegalArgumentException("Unable to verify client data.");
            }
        }
        JsonNode payload = getPayload(clientData);
        return VoxelwindServer.MAPPER.convertValue(payload, ClientData.class);
    }

    // ¯\_(ツ)_/¯
    private JsonNode getHeader(String token) throws IOException {
        String payload = token.split("\\.")[0];
        return VoxelwindServer.MAPPER.readTree(Base64.getDecoder().decode(payload));
    }

    private JsonNode getPayload(String token) throws IOException {
        String payload = token.split("\\.")[1];
        return VoxelwindServer.MAPPER.readTree(Base64.getDecoder().decode(payload));
    }

    private boolean verify(PublicKey key, JWSObject object) throws JOSEException {
        JWSVerifier verifier = new DefaultJWSVerifierFactory().createJWSVerifier(object.getHeader(), key);
        return object.verify(verifier);
    }
}
