package org.ocklin.oidc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;

class ClientCredentialFlow {

    /* 
        Client Credentials Flow

        retrieve the client's access token from Keycloak which
        is used to verify and authorize the client application
    */
    public String getAuthorizationCode() throws Exception {

        String tokenEP = Config.getTokenEndpoint();
        String responseType = "client_credentials";

        Session session = Session.getSession();

        String q = "";
        q += "grant_type" + "=" + responseType;
        q += "&" + "client_id" + "=" + session.getClientId();
        q += "&" + "client_secret" + "=" + session.getClientSecret();

        System.out.println(q);

        // reminder: POST does do parameters in body, not URI
        HttpClient client = Client.getHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("User-Agent", "CurityExample/1.0")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept",
                        "application/json,text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8 ")
                .POST(BodyPublishers.ofString(q))
                .uri(URI.create(tokenEP))
                .build();

        HttpResponse<?> response = null;
        String body = "";
        try {
            response = client.send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if(response != null) {
            body = response.body().toString();
            System.out.println(response.statusCode() + " " + body);
        }

        return Tools.extractAccessToken(body);
    }

    public static void main(String args[]){  
        ClientCredentialFlow oidc = new ClientCredentialFlow();
        String accessToken = null;
        try {
            accessToken = oidc.getAuthorizationCode();
        } catch (Exception e) {
            System.out.println("Could not fetch configuration from Keycloak due to " + e.getMessage());
        }

        JWSVerifier jwsVerifier = new JWSVerifier();

        try {
            jwsVerifier.verifyAccessToken(accessToken);

            System.out.println("Access token verified");

        } catch (MalformedURLException | ParseException | BadJOSEException | JOSEException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }  
}
