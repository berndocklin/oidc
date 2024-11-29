package org.ocklin.oidc.sasl;

import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public class OAuthSaslClient implements SaslClient {

    @Override
    public String getMechanismName() {
        return "OAUTHBEARER";
    }

    @Override
    public boolean hasInitialResponse() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasInitialResponse'");
    }

    @Override
    public byte[] evaluateChallenge(byte[] challenge) throws SaslException {
        // returning JWT token received

        throw new UnsupportedOperationException("Unimplemented method 'evaluateChallenge'");
    }

    @Override
    public boolean isComplete() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isComplete'");
    }

    @Override
    public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unwrap'");
    }

    @Override
    public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'wrap'");
    }

    @Override
    public Object getNegotiatedProperty(String propName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNegotiatedProperty'");
    }

    @Override
    public void dispose() throws SaslException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dispose'");
    }

}
