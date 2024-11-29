package org.ocklin.oidc;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.sun.net.httpserver.*;

/*
 * Authorization Code Flow
 * 
 * returning here from Keycloak after successful login
 * 
 * retrieve the user's access token from Keycloak and 
 * verify it against the certificates retrieved from the JWKS URI
 */
public class CallbackHandler implements HttpHandler {

        private String getTokenUri(Session session, String code) {

            String q = "client_id=" + session.getClientId() +
                    "&client_secret=" + URLEncoder.encode(session.getClientSecret(), StandardCharsets.UTF_8) +
                    "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                    "&code_verifier=" + URLEncoder.encode(session.getCodeVerifier(), StandardCharsets.UTF_8) +
                    "&redirect_uri=" + URLEncoder.encode(Config.getRedirectUri(), StandardCharsets.UTF_8) +
                    "&grant_type=" + "authorization_code";

            return q;
        }

        /*
         * Authorization Code Flow
         * retrive the user's access token from Keycloak
         * 
         * using 1) the code challenge as session identifier, created on this server and 
         * 2) the code identifying the Keycloak loging as returned from Keycloak via callback 
         */
        private String getAccessToken(Session session, String code) {

            String uri = Config.getTokenEndpoint();

            String q = getTokenUri(session, code);

            // reminder: POST does do parameters in body, not URI
            HttpClient client = Client.getHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .header("User-Agent", "CurityExample/1.0")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept",
                            "application/json,text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8 ")
                    .POST(BodyPublishers.ofString(q))
                    .uri(URI.create(uri))
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

        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("Callback Handle");
            Session session = Session.getSession();

            Headers h = t.getRequestHeaders();
            for (String k : h.keySet()) {
                System.out.println(k + " " + h.get(k));
            }

            String code = extractCode(t);

            System.out.println("Code: " + code);
            String accessToken = getAccessToken(session, code);

            JWSVerifier jwsVerifier = new JWSVerifier();
            String response = "Back to the actual page";

            try {
                jwsVerifier.verifyAccessToken(accessToken);
            } catch (MalformedURLException | ParseException | BadJOSEException | JOSEException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = e.getLocalizedMessage();
            }

            t.sendResponseHeaders(200, response.length());

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }


        /*
        * extract the code from the Keycloak's callback request query
        */
        private String extractCode(HttpExchange t) {

            String request = t.getRequestURI().getQuery();
            System.out.println("Callback request query:" + request);

            // extract the code parameter needed to fetch the token
            String code = "";
            String[] queryParams = request.split("&");
            for (String param : queryParams) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && keyValue[0].equals("code")) {
                    code = keyValue[1];
                    break;
                }
            }
            return code;
        }
    }
