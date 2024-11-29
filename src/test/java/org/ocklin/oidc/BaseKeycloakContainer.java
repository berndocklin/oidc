package org.ocklin.oidc;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.shaded.org.apache.commons.io.FilenameUtils;
import org.testcontainers.utility.MountableFile;

public class BaseKeycloakContainer<SELF extends BaseKeycloakContainer<SELF>> extends GenericContainer<SELF> {
    
        public static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.0.5";
        private static final String KEYCLOAK_HOME_DIR = "/opt/keycloak";
        private static final String DEFAULT_REALM_IMPORT_FILES_LOCATION = KEYCLOAK_HOME_DIR + "/data/import/";

        public BaseKeycloakContainer() {
            this(KEYCLOAK_IMAGE);
        }
    
        public BaseKeycloakContainer(String dockerImageName) {
            super(dockerImageName);
            withExposedPorts(8080);
            withEnv("KC_BOOTSTRAP_ADMIN_USERNAME", "foo");
            withEnv("KC_BOOTSTRAP_ADMIN_PASSWORD", "bar");

            String importFile = "/hcd-realm.json";
            // withEnv("KEYCLOAK_IMPORT", "/tmp/hcd-realm.json");

            String importFileInContainer = DEFAULT_REALM_IMPORT_FILES_LOCATION + FilenameUtils.getName(importFile);
            withCopyFileToContainer(MountableFile.forClasspathResource(importFile, 0644), importFileInContainer);

            setCommand("start-dev --import-realm");

        }
    
        public BaseKeycloakContainer<SELF> withRealmImportFile(String realmImportFile) {
            withEnv("KEYCLOAK_IMPORT", "/tmp/" + realmImportFile);
            withCopyFileToContainer(MountableFile.forHostPath(realmImportFile), "/tmp/" + realmImportFile);
            return self();
        }
    
        public String getUrl() {
            return "http://" + getHost() + ":" + getMappedPort(8080);
        }
    
        public String getUrlForRealm(String realm) {
            return getUrl() + "/realms/" + realm;
        }
    
        public String getUrlForPath(String path) {
            return getUrl() + path;
        }
    
        public String getUrlForRealmPath(String realm, String path) {
            return getUrlForRealm(realm) + path;
        }
    
}
