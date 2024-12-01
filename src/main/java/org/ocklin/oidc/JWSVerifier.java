package org.ocklin.oidc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;


public class JWSVerifier {

    JWSVerifier() {

        String uri = Config.getJWSUri();

        HttpClient client = Client.getHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("User-Agent", "CurityExample/1.0")
                .header("Accept", "application/json")
                .uri(URI.create(uri))
                .build();
    
        HttpResponse<?> response;
        String body = "";
        try {
            response = client.send(request, BodyHandlers.ofString());
            body = response.body().toString();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void verifyAccessToken(String accessToken) 
        throws MalformedURLException, ParseException, BadJOSEException, JOSEException {

        // Create a JWT processor for the access tokens
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        // Set the required "typ" header "at+jwt" for access tokens
        jwtProcessor.setJWSTypeVerifier(
            new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("jwt")));

        // The public RSA keys to validate the signatures will be sourced from the
        // OAuth 2.0 server's JWK set URL. The key source will cache the retrieved
        // keys for 5 minutes. 30 seconds prior to the cache's expiration the JWK
        // set will be refreshed from the URL on a separate dedicated thread.
        // Retrial is added to mitigate transient network errors.
        JWKSource<SecurityContext> keySource = null;
        URL url = new URL(Config.getJWSUri());
        keySource = JWKSourceBuilder
            .create(url)
            .retrying(true)
            .build();


        // The expected JWS algorithm of the access tokens (agreed out-of-band)
        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;

        // Configure the JWT processor with a key selector to feed matching public
        // RSA keys sourced from the JWK set URL
        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
            expectedJWSAlg,
            keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        // Set the required JWT claims for access tokens
        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
            new JWTClaimsSet.Builder().issuer(Config.getIssuer()).build(),
            new HashSet<>(Arrays.asList(
                JWTClaimNames.SUBJECT,
                JWTClaimNames.ISSUED_AT,
                JWTClaimNames.EXPIRATION_TIME,
                "scope",
                JWTClaimNames.JWT_ID))));

        // Process the token
        SecurityContext ctx = null; // optional context parameter, not required here
        JWTClaimsSet claimsSet;
        claimsSet = jwtProcessor.process(accessToken, ctx);

        // Print out the token claims set
        System.out.println("Claim set:");
        System.out.println(claimsSet.toJSONObject());
    }

    public static void main(String[] args) {
        // The access token to validate, typically submitted with an HTTP header like
        // Authorization: Bearer eyJraWQiOiJDWHVwIiwidHlwIjoiYXQrand0IiwiYWxnIjoiU...
        String at2 = 
            "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI1ZXQ3US01eE5Cd2N6QjkyRlE1bHN2Q" + 
            "0R6bkZ2Vkp1SmVEd3ZzdS04OXZFIn0.eyJleHAiOjE3MzA1ODEyMjUsImlhdCI6MTczMDU4MDkyNSwiYX" + 
            "V0aF90aW1lIjoxNzMwNTgwOTI1LCJqdGkiOiJjNGIwMGJjMi1iNTRjLTQ2OTUtOTk5Ni0yZDIwZjBlYzZ" + 
            "hNTgiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL2hjZCIsImF1ZCI6ImFjY291bnQiL" + 
            "CJzdWIiOiI4NWRhZjEyYi0xOGZjLTRhNmQtOWIxMi1hNWY1MTgyYjhlNDYiLCJ0eXAiOiJCZWFyZXIiLCJ" + 
            "henAiOiJteWNsaWVudCIsInNpZCI6ImVhZjkxNzExLWI3YzgtNDYyMy05YjNhLTVmZDJhNjBjOTMwYSIsI" + 
            "mFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo1NDQzLyIsImh0dHBzOi8" + 
            "vbG9jYWxob3N0OjU0NDMvIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLWhjZ" + 
            "CIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJ" + 
            "hY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2a" + 
            "WV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBhZGRyZXNzIHByb2ZpbGUgcGhvbmUgZW1haWwiLCJ" + 
            "hZGRyZXNzIjp7fSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiYmVybmhhcmQgb2NrbGluIiwic" + 
            "HJlZmVycmVkX3VzZXJuYW1lIjoibXl1c2VyIiwiZ2l2ZW5fbmFtZSI6ImJlcm5oYXJkIiwiZmFtaWx5X25" + 
            "hbWUiOiJvY2tsaW4iLCJlbWFpbCI6ImJlcm5kQG9ja2xpbi5kZSJ9.nB5Up-SX0ivNjo8QA_zbgZ5OWcI9" + 
            "GYVPNFviLDsa7OvszGn-wP7i_amdOGwRPjuHgZvLs--gvxp3TsqjrDt-pV1o6xNmLCYLi_SyQ53pHNU-4Z" + 
            "kryN-MVSpO6NFK1OkHwD_jJkRJ2GnUZQd4H6u8FQ922iVpX96_5X4gAE0x1nzFhSOmT9edwwTeaVsaQ6SW" + 
            "FQhaTeLMTza3zF2LEqETh7IgxUVS-N55v7tv6Jp9s8r99B8aaWz18y6VNbNoD1aPRx328-2opm4MPFrCOP" + 
            "OzkJjzaHekxO08iuASuFXVhNqiL-jExO95hRdmpi3RBqW7c-7BuAb85ltLaeYUQ7NvUw";

        JWSObject jwsObject = null;


        try {
            jwsObject = JWSObject.parse(at2);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        // continue with header and payload extraction...
        System.out.println("accessToken:");
        System.out.println(jwsObject.getHeader().toString());
        System.out.println(jwsObject.getPayload().toString());

        JWSVerifier jwsVerifier = new JWSVerifier();
        try {
            jwsVerifier.verifyAccessToken(at2);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadJOSEException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JOSEException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}