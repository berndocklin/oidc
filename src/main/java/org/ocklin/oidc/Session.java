package org.ocklin.oidc;

public class Session {

    // doing a singleton here
    private static Session instance = null;

    private Session(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public static Session createSession(String clientId, String clientSecret) {
        if(instance == null) {
            instance = new Session(clientId, clientSecret);
        }
        return instance;
    }

    public static Session getSession() {
        return instance;
    }

    String clientId;
    String clientSecret;

    String codeVerifier;

    public String getCodeVerifier() {
        return codeVerifier;
    }

    public void setCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }

    public String getClientId() {
        return clientId;
    }
    public String getClientSecret() {
        return clientSecret;
    }
}
