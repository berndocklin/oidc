package org.ocklin.oidc;

public class KeycloakContainer extends BaseKeycloakContainer<KeycloakContainer> {

    public KeycloakContainer() {
        super();
    }

    public KeycloakContainer(String dockerImageName) {
        super(dockerImageName);
    }

}

