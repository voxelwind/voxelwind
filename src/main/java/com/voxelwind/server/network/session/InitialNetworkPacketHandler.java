package com.voxelwind.server.network.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.base.Preconditions;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.network.handler.NetworkPacketHandler;
import com.voxelwind.server.network.mcpe.packets.*;
import com.voxelwind.server.network.session.auth.JwtPayload;
import com.voxelwind.server.network.util.EncryptionUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class InitialNetworkPacketHandler implements NetworkPacketHandler {
    private static final boolean USE_ENCRYPTION = false;
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

    private final UserSession session;

    public InitialNetworkPacketHandler(UserSession session) {
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

            if (USE_ENCRYPTION) {
                // Get the key to use for encrypting the connection
                PublicKey key = getKey(payload.getIdentityPublicKey());

                // ...and begin encrypting the connection.
                byte[] token = EncryptionUtil.generateRandomToken();
                byte[] serverKey = EncryptionUtil.getServerKey(key, token);

                // TODO: Fix encryption later
                session.enableEncryption(serverKey);
                session.sendUrgentPackage(EncryptionUtil.createHandshakePacket(token));
            } else {
                // Will not use encryption - initialize the player's session
                initializePlayerSession();
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
            LOGGER.error("Unable to enable encryption", e);
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

    private void initializePlayerSession() {
        McpePlayStatus status = new McpePlayStatus();
        status.setStatus(McpePlayStatus.Status.LOGIN_SUCCESS);
        session.addToSendQueue(status);

        PlayerSession playerSession = session.initializePlayerSession(session.getServer().getDefaultLevel());
        session.setHandler(playerSession.getPacketHandler());
        playerSession.doInitialSpawn();
    }

    private JwtPayload validateChainData(JsonNode data) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        Preconditions.checkArgument(data.getNodeType() == JsonNodeType.ARRAY, "chain data provided is not an array");

        if (data.size() == 1 || !USE_ENCRYPTION) {
            // The data is self-signed. We'll have to take the client at face value.
            JsonNode payload = getPayload(data.get(data.size() - 1).asText());
            JwtPayload jwtPayload = VoxelwindServer.MAPPER.convertValue(payload, JwtPayload.class);
            System.out.println("[Payload] " + payload);
            // Don't provide an XUID
            jwtPayload.getExtraData().setXuid(null);
            return jwtPayload;
        } else {
            // The data has been signed by Mojang. Validate the chain.
            Jwt<Header, Claims> last;
            for (JsonNode node : data) {
                PublicKey currentKey = getKey(getHeader(node.asText()).get("x5u").asText());
                last = Jwts.parser()
                        .setSigningKey(currentKey)
                        .parseClaimsJwt(node.asText());
            }

            JsonNode payload = getPayload(data.get(data.size() - 1).asText());
            System.out.println("[Payload] " + payload);
            return VoxelwindServer.MAPPER.convertValue(payload, JwtPayload.class);
        }
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
}
