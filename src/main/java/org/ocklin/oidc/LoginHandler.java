package org.ocklin.oidc;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.sun.net.httpserver.*;

public class LoginHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) throws IOException {
        System.out.println("Login handler");
        redirectLogin(t);
    }

    /*  buildRedirectURI
     *
     *  build the redirect URI for the client
     *  from the generated code challenge as session identifier
     * 
     *  @return the redirect URI
     */
    public String buildRedirectURI() {

        String responseType = "code";

        String authorizationEndpoint = Config.getAuthorizationEndpoint();

        String code_verifier = Tools.getSaltString(100);
        Session session = Session.getSession();
        session.setCodeVerifier(code_verifier);

        MessageDigest digest;
        String codeChallengeStr = "";

        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(code_verifier.getBytes(StandardCharsets.UTF_8));
            byte[] codeChallenge = Base64.getUrlEncoder().encode(hash);

            codeChallengeStr = new String(codeChallenge, StandardCharsets.UTF_8);
            codeChallengeStr = codeChallengeStr.split("=")[0];

            codeChallengeStr = URLEncoder.encode(codeChallengeStr, StandardCharsets.UTF_8);
            
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // generate a random string
        String state = Tools.getSaltString(20);
        String nonce = Tools.getSaltString(20);

        String q = "scope" + "=" + URLEncoder.encode(Config.scope, StandardCharsets.UTF_8);
        q += "&" + "response_type" + "=" + responseType;
        q += "&" + "client_id" + "=" + session.getClientId();
        q += "&" + "state" + "=" + URLEncoder.encode(state, StandardCharsets.UTF_8);
        q += "&" + "code_challenge" + "=" + codeChallengeStr;
        q += "&" + "code_challenge_method" + "=" + "S256";
        q += "&" + "redirect_uri" + "=" + URLEncoder.encode(Config.getRedirectUri(), StandardCharsets.UTF_8);
        q += "&" + "nonce" + "=" + URLEncoder.encode(nonce, StandardCharsets.UTF_8);

        return authorizationEndpoint + "?" + q;
    }

    /*  Authorization Code Flow
     *
     *  initiate the authorization code flow 
     * 
     *  generate a code (challenge) for verification and 
     *  redirect the client to this server's callback URL
     * 
     *  provide the callback URL 
     */
    private void redirectLogin(HttpExchange t) throws IOException {

        Headers h = t.getRequestHeaders();
        for(String k : h.keySet()) {
            System.out.println(k + " " + h.get(k));
        }

        String uri = buildRedirectURI();
        System.out.println("URI: " + uri);

        Headers responseHeaders = t.getResponseHeaders(); 
        responseHeaders.set("Location", uri); 
        t.sendResponseHeaders(302,0);
    }
}
