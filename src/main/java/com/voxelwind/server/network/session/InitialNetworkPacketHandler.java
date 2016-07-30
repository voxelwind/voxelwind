package com.voxelwind.server.network.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.server.network.handler.NetworkPacketHandler;
import com.voxelwind.server.network.mcpe.packets.*;
import com.voxelwind.server.network.session.auth.JwtPayload;
import com.voxelwind.server.network.util.EncryptionUtil;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import com.voxelwind.server.VoxelwindServer;
import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class InitialNetworkPacketHandler implements NetworkPacketHandler {
    private static final String MOJANG_PUBLIC_KEY_BASE64 =
            "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE8ELkixyLcwlZryUQcu1TvPOmI2B7vX83ndnWRUaXm74wFfa5f/lwQNTfrLVHa2PmenpGI6JhIMUJaWZrjmMj90NoKNFSNBuKdm8rYiXsfaz3K36x/1U26HpG0ZxK/V1V";
    private static final PublicKey MOJANG_PUBLIC_KEY;
    private static final Logger LOGGER = LogManager.getLogger(InitialNetworkPacketHandler.class);

    static {
        try {
            MOJANG_PUBLIC_KEY = getKey(MOJANG_PUBLIC_KEY_BASE64);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new AssertionError(e);
        }
    }

    private final UserSession session;

    public InitialNetworkPacketHandler(UserSession session) {
        this.session = session;
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
            // Get the key to use for encrypting the connection
            PublicKey key = getKey(payload.getIdentityPublicKey());

            // ...and begin encrypting the connection.
            //byte[] token = EncryptionUtil.generateRandomToken();
            //byte[] serverKey = EncryptionUtil.getServerKey(key, token);

            // TODO: Fix encryption later
            //session.enableEncryption(serverKey);
            //session.sendUrgentPackage(EncryptionUtil.createHandshakePacket(token));
            //McpeDisconnect disconnect = new McpeDisconnect();
            //disconnect.setMessage("This is a test.");
            //session.sendUrgentPackage(disconnect);

            McpePlayStatus status = new McpePlayStatus();
            status.setStatus(McpePlayStatus.Status.LOGIN_SUCCESS);
            session.sendUrgentPackage(status);

            // Put the player in the default level
            PlayerSession playerSession = session.initializePlayerSession();
            session.setHandler(playerSession.getPacketHandler());
            playerSession.doInitialSpawn(session.getServer().getDefaultLevel());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
            LOGGER.error("Unable to enable encryption", e);
        }
    }

    @Override
    public void handle(McpeRequestChunkRadius packet) {
        throw new IllegalStateException("Got unexpected McpeRequestChunkRadius");
    }

    @Override
    public void handle(McpePlayerAction packet) {
        throw new IllegalStateException("Got unexpected McpePlayerAction");
    }

    private JwtPayload validateChainData(JsonNode data) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        Preconditions.checkArgument(data.getNodeType() == JsonNodeType.ARRAY, "chain data provided is not an array");

        /*if (data.size() == 1) {
            // The data is self-signed. We'll have to take the client at face value.
            JsonNode payload = getPayload(data.get(0).asText());
            JwtPayload jwtPayload = VoxelwindServer.MAPPER.convertValue(payload, JwtPayload.class);
            System.out.println("[Payload] " + payload);
            Preconditions.checkArgument(jwtPayload.getExtraData().getXuid() == null, "Self-signed client tried to provide an XUID");
            return jwtPayload;
        } else {
            // The data has been signed by Mojang. Validate the chain.
            PublicKey currentKey = MOJANG_PUBLIC_KEY;
            Jwt<Header, String> last;
            for (JsonNode node : data) {
                last = Jwts.parser()
                        .setSigningKey(currentKey)
                        .parsePlaintextJwt(node.asText());

                currentKey = getKey((String) last.getHeader().get("x5u"));
            }

            JsonNode payload = getPayload(data.get(data.size() - 1).asText());
            System.out.println("[Payload] " + payload);
            return VoxelwindServer.MAPPER.convertValue(payload, JwtPayload.class);
        }*/
        JsonNode payload = getPayload(data.get(data.size() - 1).asText());
        JwtPayload jwtPayload = VoxelwindServer.MAPPER.convertValue(payload, JwtPayload.class);
        System.out.println("[Payload] " + payload);
        //Preconditions.checkArgument(jwtPayload.getExtraData().getXuid() == null, "Self-signed client tried to provide an XUID");
        return jwtPayload;
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

    private static PublicKey getKey(String b64) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        return KeyFactory.getInstance("EC", "BC").generatePublic(
                new X509EncodedKeySpec(Base64.getDecoder().decode(b64)));
    }
}
