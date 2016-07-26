package com.voxelwind.server.network.session.auth;

public class JwtPayload {
    private int exp;
    private UserAuthenticationProfile extraData;
    private String identityPublicKey;
    private int nbf;

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public UserAuthenticationProfile getExtraData() {
        return extraData;
    }

    public void setExtraData(UserAuthenticationProfile extraData) {
        this.extraData = extraData;
    }

    public String getIdentityPublicKey() {
        return identityPublicKey;
    }

    public void setIdentityPublicKey(String identityPublicKey) {
        this.identityPublicKey = identityPublicKey;
    }

    public int getNbf() {
        return nbf;
    }

    public void setNbf(int nbf) {
        this.nbf = nbf;
    }
}
