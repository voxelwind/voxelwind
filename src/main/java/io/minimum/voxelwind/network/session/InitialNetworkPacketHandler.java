package io.minimum.voxelwind.network.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.minimum.voxelwind.VoxelwindServer;
import io.minimum.voxelwind.network.handler.NetworkPacketHandler;
import io.minimum.voxelwind.network.mcpe.packets.McpeLogin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InitialNetworkPacketHandler implements NetworkPacketHandler {
    private static final String MOJANG_PUBLIC_KEY =
            "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE8ELkixyLcwlZryUQcu1TvPOmI2B7vX83ndnWRUaXm74wFfa5f/lwQNTfrLVHa2PmenpGI6JhIMUJaWZrjmMj90NoKNFSNBuKdm8rYiXsfaz3K36x/1U26HpG0ZxK/V1V";

    @Override
    public void handle(McpeLogin login) {
        JsonNode certData;
        try {
            certData = VoxelwindServer.MAPPER.readTree(login.getChainData());
        } catch (IOException e) {
            throw new RuntimeException("Certificate JSON can not be read.");
        }

        // Verify the JWT chain data.
        JsonNode certChainData = certData.get("chain").get("chain");
        if (certChainData.getNodeType() != JsonNodeType.ARRAY) {
            throw new RuntimeException("Certificate data is not valid");
        }

        List<Jwt<Header, String>> chainJwtHeaders = new ArrayList<>();
        String currentKey = MOJANG_PUBLIC_KEY;
        for (JsonNode node : certChainData) {
            Jwt<Header, String> jwt = Jwts.parser()
                    .setSigningKey(MOJANG_PUBLIC_KEY)
                    .parsePlaintextJwt(node.asText());
            System.out.println(jwt.toString());
        }
    }
}
