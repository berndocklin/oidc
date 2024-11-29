package org.ocklin.oidc.sasl;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;

import org.apache.commons.lang3.ArrayUtils;

import javax.security.sasl.SaslServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.Map;

public class OAuthSaslServer implements SaslServer {
    private String mechanism;
    private boolean complete = false;

    public OAuthSaslServer(String mechanism) {
        this.mechanism = mechanism;
    }

    @Override
    public String getMechanismName() {
        return mechanism;
    }

    @Override
    public byte[] evaluateResponse(byte[] response) throws SaslException {
        // Implement your OAuth token validation logic here
        complete = true;
        return null;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public String getAuthorizationID() {
        if (!complete) {
            throw new IllegalStateException("Authentication not completed");
        }
        return "authorizedUser"; // Return the authorized user ID
    }

    @Override
    public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException {
        throw new IllegalStateException("Not supported");
    }

    @Override
    public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
        throw new IllegalStateException("Not supported");
    }

    @Override
    public Object getNegotiatedProperty(String propName) {
        return null;
    }

    @Override
    public void dispose() throws SaslException {
        // Clean up resources
    }

    public static class ClientFactory implements SaslClientFactory {
        public class ClientCallbackHandler implements CallbackHandler {
            @Override
            public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                for (Callback callback : callbacks) {
                    if (callback instanceof NameCallback) {
                        NameCallback nameCallback = (NameCallback) callback;
                        nameCallback.setName("username");
                    } else if (callback instanceof PasswordCallback) {
                        PasswordCallback passwordCallback = (PasswordCallback) callback;
                        passwordCallback.setPassword("password".toCharArray());
                    } else {
                        throw new UnsupportedCallbackException(callback);
                    }
                }
            }
        }

        @Override
        public SaslClient createSaslClient(String[] mechanisms, String authorizationId, String protocol, String serverName,
                Map<String, ?> props, CallbackHandler cbh) throws SaslException {
            for (int i = 0; i < mechanisms.length; i++) {
                if (mechanisms[i].equals("OAUTHBEARER"))
                    return new OAuthSaslClient();
            }
            return Sasl.createSaslClient(mechanisms, null, "http", 
                "example.com", null, new ClientCallbackHandler());
        }

        @Override
        public String[] getMechanismNames(Map<String, ?> props) {
            return new String[] { "OAUTHBEARER" };
        }
    }
 
    public static void run() {

        byte[] challenge;
        byte[] response;

        String[] mechanisms = new String[]{"OAUTHBEARER"}; 

        SaslServer saslServer = null;
        SaslClient saslClient = null;

        OAuthSaslServer.ClientFactory cf = new OAuthSaslServer.ClientFactory();
        try {
            saslServer = new OAuthSaslServer("OAUTHBEARER");
            saslClient = cf.createSaslClient(mechanisms, null, "http", "example.com", null, null);
        } catch (SaslException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
     
        try {
            challenge = saslServer.evaluateResponse("OAUTHBEARER".getBytes(StandardCharsets.UTF_8));
            System.out.println("Challenge: " + challenge);            
            response = saslClient.evaluateChallenge(challenge);

            if (response != null) {
                System.out.println("Response: " + new String(response));
                // The incoming SASL plain text message is in the form: [authz] 0 authn 0 password
                int split1 = ArrayUtils.indexOf(response, (byte) 0);
                int split2 = ArrayUtils.indexOf(response, (byte) 0, split1 + 1);

                System.out.println(new String(response, split1 + 1, split2 - split1 - 1, StandardCharsets.UTF_8) + ", " +
                                          new String(response, split2 + 1, response.length - split2 - 1, StandardCharsets.UTF_8)  + ", " +
                                          new String(response, 0, split1, StandardCharsets.UTF_8));
            }
            
            challenge = saslServer.evaluateResponse(response);
            response = saslClient.evaluateChallenge(challenge);
        } catch (SaslException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
     
        //assertTrue(saslServer.isComplete());
        //assertTrue(saslClient.isComplete());

    }    

    public static void main(String[] args) {
        OAuthSaslServer.run();
    }
}
