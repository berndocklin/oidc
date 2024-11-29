package org.ocklin.oidc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class KeycloakTest {
    @Test
    public void shouldJustRunForFun() {
        KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.0.5");
        String url = null;

        try  {
            keycloak.start();
            url = keycloak.getUrlForRealm("hcd");
            System.out.println("Keycloak is running at: " +  url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String passOnUrl = url;
        assertDoesNotThrow(() -> { Config.init(passOnUrl); });

        try {
            Thread.sleep(1200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Leaving keycloak");

        keycloak.close();



    }

}
