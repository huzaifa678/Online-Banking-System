package com.project.user.config;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.OAuth2Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KeyCloakConfig {

    @Value("${app.config.keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${app.config.keycloak.realm}")
    private String clientRealm;

    @Value("${app.config.keycloak.credentials.secret}")
    private String clientSecretKey;

    @Value("${app.config.keycloak.resource}")
    private String clientId;

    @Bean
    public Keycloak adminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(clientRealm)
                .clientId(clientId)
                .clientSecret(clientSecretKey)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
