package org.ocklin.oidc;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Config {

    static String issuer = "http://localhost:8080/realms/hcd";
    static String scope = "openid profile email address phone";
    String send_parameters_via = "query";
    
    static String jwsUri;
    static String authorizationEndpoint;
    static String endSessionEndpoint;
    static String tokenEndpoint;

    public static void setEndSessionEndpoint(String endSessionEndpoint) {
        Config.endSessionEndpoint = endSessionEndpoint;
    }
    public static String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }
    public static void setAuthorizationEndpoint(String authorizationEndpoint) {
        Config.authorizationEndpoint = authorizationEndpoint;
    }
    public static void setJWSUri(String jwsUri) {
        Config.jwsUri = jwsUri;
    }

    // make sure this is a valid uri set as callback in keycloak client
    static String redirectUri = "http://localhost:5443/callback";

    public static String getRedirectUri() {
        return redirectUri;
    }

    public static String getIssuer() {
        return issuer;
    }

    public static String getJWSUri() {
        return jwsUri;
    }

    public static String getEndSessionEndpoint() {
        return endSessionEndpoint;
    }

    public static String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public static void setTokenEndpoint(String tokenEndpoint) {
        Config.tokenEndpoint = tokenEndpoint;
    }

    // get the metadata from the OIDC/Keycloak server
    // TODO: cleanup, as this function moved from OIDC
    static public void init(String kcUrl) throws Exception {

        // validate url, let it throw exceptions
        new URL(kcUrl).toURI().getPath();

        issuer = kcUrl;

        String metaDataUrl = issuer + "/.well-known/openid-configuration";

        System.out.println("Fetching metadata from: " + metaDataUrl);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(metaDataUrl))
                .build();
    
        HttpResponse<String> response;
        String json = null;
        try {
            response = client.send(request, BodyHandlers.ofString());
            json = response.body();
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(json);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JsonNode ae = rootNode.findValue("authorization_endpoint");
        JsonNode te = rootNode.findValue("token_endpoint");
        JsonNode ju = rootNode.findValue("jwks_uri");
        JsonNode ese = rootNode.findValue("end_session_endpoint");

        if(ae == null) {
            throw new Exception("Keycloak JWKS URI not found.");
        }
        if(ju == null) {
            throw new Exception("Keycloak authorisation endpoint or JWKS URI not found.");
        }
        if(te == null) {
            throw new Exception("Keycloak token endpoint not found.");
        }
        if(ese == null) {
            throw new Exception("Keycloak end session endpoint not found.");
        }

        setAuthorizationEndpoint(ae.asText());
        setJWSUri(ju.asText());        
        setEndSessionEndpoint(ese.asText());
        setTokenEndpoint(te.asText());

        System.out.println("Authorization endpoint: " + getAuthorizationEndpoint());
        System.out.println("JWKS URI: " + getRedirectUri());
        System.out.println("End session endpoint: " + getEndSessionEndpoint());
        System.out.println("Token endpoint: " + getTokenEndpoint());
    }
}
