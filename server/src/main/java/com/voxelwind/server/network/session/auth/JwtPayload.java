package com.voxelwind.server.network.session.auth;

import lombok.Data;

@Data
public class JwtPayload {
    private int exp;
    private UserAuthenticationProfile extraData;
    private String identityPublicKey;
    private int nbf;
    private int randomNonce;
    private String iss;
    private int iat;
}
